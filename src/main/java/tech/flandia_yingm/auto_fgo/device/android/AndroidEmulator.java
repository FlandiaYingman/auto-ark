package tech.flandia_yingm.auto_fgo.device.android;

import com.google.common.io.ByteStreams;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;
import tech.flandia_yingm.auto_fgo.device.Device;
import tech.flandia_yingm.auto_fgo.img.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class AndroidEmulator implements Device {

    @NonNull
    private final JadbDevice device;

    private final int width;

    private final int height;


    public AndroidEmulator(String serial) throws IOException, JadbException {
        connect(serial);
        val connection = new JadbConnection();
        while (true) {
            val optionalDevice = connection.getDevices().stream()
                                           .filter(device -> device.getSerial().equals(serial))
                                           .findAny();
            if (optionalDevice.isPresent()) {
                device = optionalDevice.get();
                break;
            }
        }
        val capture = capture();
        width = capture.getWidth();
        height = capture.getHeight();
    }

    private static void connect(String serial) throws IOException {
        val process = new ProcessBuilder("adb", "connect", serial).start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            //Ignored: thread interrupted
            process.destroyForcibly();
        }
    }


    @Override
    public void tap(Point point) {
        //point = Point.map(point, 0, Short.MAX_VALUE, 0, width, 0, Short.MAX_VALUE, 0, height);
        try {
            log.debug("{} - Touching the screen at point {}", this, point);
            val shellStream = device.executeShell("input", "tap", String.valueOf(point.getX()), String.valueOf(point.getY()));
            handleShellStream(shellStream);
            log.debug("{} - Touched the screen at point {}", this, point);
        } catch (IOException e) {
            log.warn("{} - An I/O error occurs while touching the screen at point {}", this, point, e);
        } catch (JadbException e) {
            log.warn("{} - An JADB error occurs while touching the screen at point {}", this, point, e);
        }
    }


    @Override
    public void swipe(Point start, Point end, long duration) {
        try {
            log.debug("{} - Swiping the screen from point {} to point {}", this, start, end);
            val shellStream = device.executeShell(
                    "input",
                    "touchscreen",
                    "swipe",
                    String.valueOf(start.getX()), String.valueOf(start.getY()), String.valueOf(end.getX()), String.valueOf(end.getY())
            );
            handleShellStream(shellStream);
            log.debug("{} - Swiped the screen from point {} to point {}", this, start, end);
        } catch (IOException e) {
            log.warn("{} - An I/O error occurs while swiping the screen from point {} to point {}", this, start, end, e);
        } catch (JadbException e) {
            log.warn("{} - An JADB error occurs while swiping the screen from point {} to point {}", this, start, end, e);
        }
    }

    @Override
    public void insert(String text) {
        text = text.replaceAll(" ", "%s");
        try {
            log.debug("{} - Inserting the text {}", this, text);
            val shellStream = device.executeShell("input", "text", text);
            handleShellStream(shellStream);
            log.debug("{} - Inserted the text {}", this, text);
        } catch (IOException e) {
            log.warn("{} - An I/O error occurs while inserting the text {}", this, text, e);
        } catch (JadbException e) {
            log.warn("{} - An JADB error occurs while inserting the text {}", this, text, e);
        }
    }

    @Override
    public BufferedImage capture() {
        try {
            log.debug("{} - Capturing the screen to the temp file: screen.png", this);
            @Cleanup val screenStream = device.executeShell("screencap", "-p", "/sdcard/screen.png");
            ByteStreams.toByteArray(screenStream);
            device.pull(new RemoteFile("/sdcard/screen.png"), new File("screen.png"));
            log.debug("{} - Captured the screen to the temp file: screen.png", this);
            return ImageIO.read(new File("screen.png"));
        } catch (IOException e) {
            log.warn("{} - An I/O error occurs while capturing the screen to the temp file", this, e);
        } catch (JadbException e) {
            log.warn("{} - An JADB error occurs while capturing the screen to the temp file", this, e);
        }
        return null;
    }

    private static void handleShellStream(InputStream shellStream) throws IOException {
        var eof = false;
        while (!eof) {
            eof = shellStream.read() == -1;
        }
        shellStream.close();
    }

    public List<AndroidEvent> getEvents() {
        log.info("{} - Reading events", this);
        try (val eventReader = new BufferedReader(new InputStreamReader(device.executeShell("getevent", "-q")))) {
            val eventList = new ArrayList<AndroidEvent>();
            var eventLine = "";
            while ((eventLine = eventReader.readLine()) != null) {
                log.info("{} - Read event: {}", this, eventLine);
                val event = AndroidEvent.parse(eventLine);
                eventList.add(event);
            }
            log.info("{} - Read all events", this);
            return eventList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void sendEvents(List<AndroidEvent> eventList) {
        try {
            for (AndroidEvent event : eventList) {
                val start = new ProcessBuilder("adb", "-s", device.getSerial(), "shell", String.format("sendevent %s %d %d %d",
                                                                                                       event.getDeviceName(),
                                                                                                       event.getEventType(),
                                                                                                       event.getEventCode(),
                                                                                                       event.getEventValue()
                )).start();
                log.info("{} - {}", this, IOUtils.toString(start.getInputStream(), Charset.defaultCharset()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void recordEvents() throws IOException {
        val recordDir = "./record";
        Files.createDirectories(Paths.get(recordDir));

        log.info("{} - Recording events", this);
        val process = Runtime.getRuntime().exec(new String[]{"adb", "shell", "getevent -q"});
        try (val br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            var line = "";
            var num = 0;
            while ((line = br.readLine()) != null) {
                val xEvent = AndroidEvent.parse(line);
                if (isXEvent(xEvent)) {
                    if ((line = br.readLine()) == null) {
                        break;
                    }
                    val yEvent = AndroidEvent.parse(line);
                    if (isYEvent(yEvent)) {
                        var point = new Point((int) xEvent.getEventValue(), (int) yEvent.getEventValue());
                        val image = String.format("./record/%d.png", num++);
                        ImageIO.write(capture(), "png", new File(image));

                        point = Point.map(point, 0, 32767, 0, 960, 0, 32767, 0, 540);
                        log.info("{} - Recorded event: new Point({}, {}) num: {}", this, point.getX(), point.getY(), num);
                    }
                }

            }
            log.info("{} - Recorded events", this);
        } finally {
            process.destroyForcibly();
        }
    }

    private static boolean isXEvent(AndroidEvent xEvent) {
        return xEvent.getDeviceName().equals("/dev/input/event8") &&
                xEvent.getEventType() == 3 &&
                xEvent.getEventCode() == 5;
    }

    private static boolean isYEvent(AndroidEvent yEvent) {
        return yEvent.getDeviceName().equals("/dev/input/event8") &&
                yEvent.getEventType() == 3 &&
                yEvent.getEventCode() == 6;
    }

}
