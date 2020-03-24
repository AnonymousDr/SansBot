package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Deslistar extends Comando{

    public Deslistar(){
        super("Quitar una canción de una lista","deslistar","unlist");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<3){
            e.getChannel().sendMessage(Boton.NUBE_BOCADILLO+" Uso: "+args[0]+" <Nombre de la lista> <Canción>").complete();
            return;
        }if(Biblioteca.deslistar(args[1],Biblioteca.procesarNombreCancion(args[2])))e.getChannel().sendMessage(Boton.FANTASMA+" ¡La canción acaba de desaparecer de la lista!").complete();
        else e.getChannel().sendMessage(Boton.ADVERTENCIA+" ¡No ha cambiado nada!").complete();
    }
}
