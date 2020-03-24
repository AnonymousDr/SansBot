package com.anderhurtado.java.discord.sans.objetos;

public class Boton{

    public static final Boton ATRAS=new Boton("\u25C0"),SEGUIR=new Boton("\u25B6"),CRUZ_ROJA=new Boton("\u274C"),NOTA_MUSICAL=new Boton("\uD83C\uDFB5"),OVNI=new Boton("\uD83D\uDEF8"),FLOPPY_DISK=new Boton("\uD83D\uDCBE"),
    MICROFONO_NOTAS=new Boton("\uD83C\uDFA4"),ENFADADO=new Boton("\uD83D\uDE20"),LLORANDO=new Boton("\uD83D\uDE22"),CD=new Boton("\uD83D\uDCBF"),ADVERTENCIA=new Boton("\u26A0"),
    NUBE_BOCADILLO=new Boton("\uD83D\uDDE8"),FANTASMA=new Boton("\uD83D\uDC7B"),NOTAS_MUSICALES=new Boton("\uD83C\uDFB6"),LUPA=new Boton("\uD83D\uDD0E");

    public final String CODIGO;

    public Boton(String codigo){
        CODIGO=codigo;
    }

    @Override
    public String toString(){
        return CODIGO;
    }

}
