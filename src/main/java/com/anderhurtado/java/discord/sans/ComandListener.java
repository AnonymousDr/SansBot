package com.anderhurtado.java.discord.sans;

import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ComandListener extends ListenerAdapter{

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e){
        if(e.getAuthor().isBot())return;
        Comando.ejecutar(e,e.getMessage().getContentRaw().split(" "));
    }
}
