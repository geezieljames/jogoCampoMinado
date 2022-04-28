package modelo

import kotlin.reflect.jvm.internal.impl.types.checker.TypeCheckingProcedureCallbacks

enum class CampoEvento {EXPLOSAO, ABERTURA, MARCACAO, DESMARCACAO, REINICIALIZACAO }

data class Campo(val linha: Int, val coluna: Int){
    private val vizinhos = arrayListOf<Campo>()
    private val callbacks = arrayListOf<(Campo,CampoEvento) -> Unit>()

    var aberto = false
    var marcado = false
    var minado = false

    val fechado :Boolean get() = !aberto
    val desmarcado :Boolean get() = !marcado
    val seguro :Boolean get() = !minado

    val objetivoAlcancado :Boolean get() = seguro && marcado || minado && marcado
    val vizinhosMinados :Int get() = vizinhos.filter { it.minado }.size
    val vizinhosSeguros :Boolean
    get() = vizinhos.map { it.seguro }.reduce { resultado, seguro -> resultado && seguro  }

    fun addVizinho(vizinho :Campo){
        vizinhos.add(vizinho)
    }
    fun onEvento(callback: (Campo,CampoEvento)->Unit){
        callbacks.add(callback)
    }

    fun abrir(){
        if(fechado){
            aberto = true
            if(minado){
                callbacks.forEach { it(this,CampoEvento.EXPLOSAO) }
            }else{
                callbacks.forEach{ it(this,CampoEvento.ABERTURA) }
             vizinhos.filter { it.fechado && it.seguro && vizinhosSeguros }.forEach { abrir() }
            }
        }
    }
    fun marcacao(){
        if(fechado){
            marcado = !marcado
            val evento = if(marcado)CampoEvento.EXPLOSAO else CampoEvento.DESMARCACAO
            callbacks.forEach { it(this, evento) }
        }
    }
    fun minar(){
        minado = true
    }
    fun reiniciar(){
        aberto = false
        marcado = false
        minado = false

        callbacks.forEach { it(this, CampoEvento.REINICIALIZACAO) }
    }

}

