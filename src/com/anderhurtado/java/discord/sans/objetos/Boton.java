package com.anderhurtado.java.discord.sans.objetos;

public class Boton{

    public static final Boton ATRAS=new Boton("\u25C0"),SEGUIR=new Boton("\u25B6");

    public final String CODIGO;

    public Boton(String codigo){
        CODIGO=codigo;
    }

    @Override
    public String toString(){
        return CODIGO;
    }

}
