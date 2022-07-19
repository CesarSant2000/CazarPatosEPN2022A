package com.santacruz.cesar.cazarpatossc

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.CountDownTimer
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var textViewUsuario: TextView
    lateinit var textViewContador: TextView
    lateinit var textViewTiempo: TextView
    lateinit var imageViewPato: ImageView
    var contador = 0
    var anchoPantalla = 0
    var alturaPantalla = 0
    var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Inicialización de variables
        textViewUsuario = findViewById(R.id.textViewUser)
        textViewContador = findViewById(R.id.textViewCounter)
        textViewTiempo = findViewById(R.id.textViewTime)
        imageViewPato = findViewById(R.id.imageViewDuck)

        //Obtener el usuario de pantalla login
        val extras = intent.extras ?: return
        val usuario = (extras.getString(EXTRA_LOGIN)?.substringBefore("@")) ?:"Unknown"
        textViewUsuario.text = usuario

        //Add ActionBar
        val actionbar = supportActionBar
        actionbar?.title = "Cazar Patos Con ActionBar"
        actionbar?.setDisplayHomeAsUpEnabled(true)

        //Determina el ancho y largo de pantalla
        inicializarPantalla()
        //Cuenta regresiva del juego
        inicializarCuentaRegresiva()
        //Evento clic sobre la imagen del pato
        imageViewPato.setOnClickListener {
            if (gameOver) return@setOnClickListener
            contador++
            MediaPlayer.create(this, R.raw.gunshot).start()
            textViewContador.text = contador.toString()
            imageViewPato.setImageResource(R.drawable.duck_clicked)
            //Evento que se ejecuta luego de 500 milisegundos
            lifecycleScope.launch {
                delay(500)
                imageViewPato.setImageResource(R.drawable.duck)
                moverPato()
            }
        }
    }
    private fun inicializarPantalla() {
        // 1. Obtenemos el tamaño de la pantalla del dispositivo
        val display = this.resources.displayMetrics
        anchoPantalla = display.widthPixels
        alturaPantalla = display.heightPixels
    }

    private fun moverPato() {
        val min = imageViewPato.width /2
        val maximoX = anchoPantalla - imageViewPato.width
        val maximoY = alturaPantalla - imageViewPato.height
        // Generamos 2 números aleatorios, para la coordenadas x , y
        val randomX = Random().nextInt(maximoX - min ) + 1
        val randomY = Random().nextInt(maximoY - min ) + 1

        // Utilizamos los números aleatorios para mover el pato a esa nueva posición
        imageViewPato.x = randomX.toFloat()
        imageViewPato.y = randomY.toFloat()
    }
    var contadorTiempo = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val segundosRestantes = millisUntilFinished / 1000
            val stringSegundosRestantes = "${segundosRestantes}s"
            textViewTiempo.text = stringSegundosRestantes
        }
        override fun onFinish() {
            textViewTiempo.text = getString(R.string.initial_time)
            gameOver = true
            mostrarDialogoGameOver()
        }
    }
    private fun inicializarCuentaRegresiva() {
        contadorTiempo.start()
    }
    private fun mostrarDialogoGameOver() {
        val builder = AlertDialog.Builder(this)
        builder
            .setIcon(R.drawable.duck_hunt_logo)
            .setMessage("Felicidades!!\nHas conseguido cazar $contador patos")
            .setTitle("Fin del juego")
            .setPositiveButton("Reiniciar"
            ) { _, _ ->
                reiniciarJuego()
            }
            .setNegativeButton("Cerrar"
            ) { _, _ ->
                //dialog.dismiss()
            }
        builder.create().show()
    }
    fun reiniciarJuego(){
        contador = 0
        gameOver = false
        contadorTiempo.cancel()
        textViewContador.text = contador.toString()
        moverPato()
        inicializarCuentaRegresiva()
    }

}

