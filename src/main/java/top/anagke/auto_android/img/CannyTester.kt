package top.anagke.auto_android.img

import javafx.application.Application
import javafx.beans.property.DoubleProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.io.ByteArrayInputStream
import java.io.File

class CannyTester : Application() {

    @FXML private lateinit var imageView1: ImageView
    @FXML private lateinit var imageView2: ImageView
    @FXML private lateinit var slider1: Slider
    @FXML private lateinit var slider2: Slider
    @FXML private lateinit var slider3: Slider
    @FXML private lateinit var label1: Label
    @FXML private lateinit var label2: Label
    @FXML private lateinit var label3: Label

    val img = Img.decode(File("Screenshot_2021.11.20_16.08.00.814.png").readBytes())!!

    @FXML
    fun initialize() {

        slider1.valueProperty().addListener { value ->
            val valueProperty = value as DoubleProperty
            label1.text = String.format("%6.2f", valueProperty.value)
            updateImage()
        }
        slider2.valueProperty().addListener { value ->
            val valueProperty = value as DoubleProperty
            label2.text = String.format("%6.2f", valueProperty.value)
            updateImage()
        }
        slider3.valueProperty().addListener { value ->
            val valueProperty = value as DoubleProperty
            label3.text = String.format("%6.2f", valueProperty.value)
            updateImage()
        }
    }

    private fun updateImage() {
        val threshold1 = slider1.value
        val threshold2 = slider2.value
        val blur = img.blur(slider3.value)
        val canny = blur.canny(threshold1, threshold2)
        imageView1.image = Image(ByteArrayInputStream(Img.encode(blur)))
        imageView2.image = Image(ByteArrayInputStream(Img.encode(canny)))
    }

    override fun start(primaryStage: Stage) {
        val fxml = this.javaClass.getResource("/top/anagke/auto_android.img/canny_tester.fxml")
        val loader = FXMLLoader(fxml)
        val root = loader.load() as BorderPane
        val scene = Scene(root)

        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(CannyTester::class.java)
        }
    }
}

