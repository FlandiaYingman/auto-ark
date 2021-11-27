package top.anagke.auto_android.auto

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import top.anagke.auto_android.img.Img
import top.anagke.auto_android.util.Pos
import top.anagke.auto_ark.adb.BlueStacks
import top.anagke.auto_ark.native.openProc
import java.io.ByteArrayInputStream
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt


class AndroidRecorder() : Application() {

    private val device = BlueStacks().open()

    private val scheduler = Executors.newSingleThreadScheduledExecutor()


    @FXML lateinit var buttonCapture: Button
    @FXML lateinit var buttonSave: Button
    @FXML lateinit var imageView: ImageView
    @FXML lateinit var tapField: TextField
    @FXML lateinit var dragField: TextField


    override fun start(primaryStage: Stage) {
        val fxml = this.javaClass.getResource("AndroidRecorder.fxml")
        val loader = FXMLLoader(fxml)
        val root = loader.load() as BorderPane
        val scene = Scene(root)

        primaryStage.scene = scene
        primaryStage.show()
    }

    @FXML
    fun initialize() {
    }

    private var mousePos = AtomicReference(Pos(0, 0) to Pos(0, 0))

    fun onMouseDown(mouseEvent: MouseEvent) {
        val (start, end) = mousePos.updateAndGet {
            Pos(mouseEvent.x.roundToInt(), mouseEvent.y.roundToInt()) to it.second
        }
        tapField.text = "tap(${start.x}, ${start.y})"
        dragField.text = "drag(${start.x}, ${start.y}, ${end.x}, ${end.y})"
    }

    fun onMouseUp(mouseEvent: MouseEvent) {
        val (start, end) = mousePos.updateAndGet {
            it.first to Pos(mouseEvent.x.roundToInt(), mouseEvent.y.roundToInt())
        }
        tapField.text = "tap(${end.x}, ${end.y})"
        dragField.text = "drag(${start.x}, ${start.y}, ${end.x}, ${end.y})"
    }

    private var capture = device.cap()

    fun onCaptureAction(actionEvent: ActionEvent) {
        capture = device.cap()
        ByteArrayInputStream(Img.encode(capture)).use {
            imageView.image = Image(it)
        }
    }

    fun onSaveAction(actionEvent: ActionEvent) {
        val imageFile = File("tmp.png")
        imageFile.writeBytes(Img.encode(capture))
        openProc("C:\\Program Files\\Adobe\\Adobe Photoshop 2020\\Photoshop.exe", imageFile.canonicalPath)
    }

}

fun main() {
    Application.launch(AndroidRecorder::class.java)
}