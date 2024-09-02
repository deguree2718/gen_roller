package com.gen_roller;

import org.javacord.api.*;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.intent.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.sticker.StickerBuilder;
import org.javacord.api.entity.sticker.StickerFormatType;
import org.javacord.api.entity.sticker.StickerItem;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        String token = System.getenv("TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).login().join();

        api.addMessageCreateListener(event -> {
            try{
                if (event.getMessageContent().startsWith("*-ajuda")){
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Ajuda")
                            .setDescription("O que posso fazer com esse bot?")
                            .setAuthor("degu")
                            .addField("*-rolar", """
                                    o padrao para o comando:  
                                    [0-9][a,b,p]|[0-9][a,b,p]|[0-9][a,b,p] [0-9][s,d,c]|[0-9][s,d,c]|[0-9][s,d,c]
                                    - a: dado de Habilidade
                                    - b: dado de Bonus
                                    - p: dado de Proficiencia
                                    
                                    - s: dado de Problema
                                    - d: dado de Dificuldade
                                    - c: dado de Desafio""")
                            .setColor(Color.CYAN);
                    event.getChannel().sendMessage(embed);
                } else if (event.getMessageContent().startsWith("*-rolar")) {
                    String msg = event.getMessageContent().substring(8);
                    String[] dice_group = msg.split(" ");
                    int success = 0;
                    int advantage = 0;
                    int triumph = 0;
                    int despair = 0;
                    for (String d : dice_group){
                        String[] dice = msg.split("\\|");
                        for (String die : dice) {
                            String type = die.substring(1);
                            int amount = Integer.parseInt(die.substring(0,1));
                            int[] results = roll(type, amount);
                            success += results[0];
                            advantage += results[1];
                            triumph += results[2];
                            despair += results[3];
                        }
                    }
                    if (success >= 1){
                        event.getChannel().sendMessage(String.format("""
                            **Voce passou!**
                            - Sucessos: %d
                            - Vantagens: %d
                            - Triunfos: %d
                            - Desesperos: %d""", success, advantage, triumph, despair));
                    } else {
                        event.getChannel().sendMessage(String.format("""
                            **Voce falhou**
                            - Sucessos: %d
                            - Vantagens: %d
                            - Triunfos: %d
                            - Desesperos: %d""", success, advantage, triumph, despair));
                    }
                    log(event);
                } else if (event.getMessageContent().startsWith(":=help")){
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Help")
                            .setDescription("What can I do with gen_roller?")
                            .setAuthor("degu")
                            .addField(":=roll", """
                                    the pattern for rolling the dice is  
                                    [0-9][a,b,p]|[0-9][a,b,p]|[0-9][a,b,p] [0-9][s,d,c]|[0-9][s,d,c]|[0-9][s,d,c]
                                    (yes the pipe (|) is mandatory)
                                    - a: Ability dice
                                    - b: Boost dice
                                    - p: Proficiency Dice
                                    
                                    - s: Setback dice
                                    - d: Difficulty dice
                                    - c: Challenge dice""")
                            .setColor(Color.CYAN);
                    event.getChannel().sendMessage(embed);
                } else if (event.getMessageContent().startsWith(":=roll")){
                    String msg = event.getMessageContent().substring(7);
                    String[] dice_group = msg.split(" ");
                    int success = 0;
                    int advantage = 0;
                    int triumph = 0;
                    int despair = 0;
                    for (String d : dice_group){
                        String[] dice = msg.split("\\|");
                        for (String die : dice) {
                            String type = die.substring(1);
                            int amount = Integer.parseInt(die.substring(0,1));
                            int[] results = roll(type, amount);
                            success += results[0];
                            advantage += results[1];
                            triumph += results[2];
                            despair += results[3];
                        }
                    }
                    if (success >= 1){
                        event.getChannel().sendMessage(String.format("""
                            **You passed!**
                            - Successes: %d
                            - Advantages: %d
                            - Triumph: %d
                            - Despair: %d""", success, advantage, triumph, despair));
                    } else {
                        event.getChannel().sendMessage(String.format("""
                            **You failed**
                            - Successes: %d
                            - Advantages: %d
                            - Triumph: %d
                            - Despair: %d""", success, advantage, triumph, despair));
                    }
                    log(event);
                }
            } catch (Exception e){
                log(e, event);
            }
        });
    }

    private static int[] roll(String type, int amount){
        int success = 0;
        int advantage = 0;
        int triumph = 0;
        int despair = 0;
        Random rng = new Random();
        int rolled = 0;
        for (var i = 0; i < amount; i++){
            switch (type){
                case "a":
                    rolled = rng.ints(1,1,8).sum();
                    switch (rolled){
                        case 2:
                            success += 2;
                            break;
                        case 3:
                            advantage += 1;
                            break;
                        case 4:
                            success += 1;
                            break;
                        case 5:
                            success += 1;
                            break;
                        case 6:
                            advantage += 1;
                            break;
                        case 7:
                            advantage += 2;
                            break;
                        case 8:
                            success += 1;
                            advantage += 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case "b":
                    rolled = rng.ints(1,1,6).sum();
                    switch (rolled){
                        case 2:
                            success += 1;
                            break;
                        case 3:
                            success += 1;
                            advantage += 1;
                            break;
                        case 4:
                            advantage += 2;
                            break;
                        case 5:
                            advantage += 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case "p":
                    rolled = rng.ints(1,1,12).sum();
                    switch (rolled){
                        case 1:
                            success += 1;
                            triumph += 1;
                            break;
                        case 2:
                            success += 1;
                            break;
                        case 3:
                            success += 2;
                            break;
                        case 4:
                            advantage += 2;
                            break;
                        case 5:
                            advantage += 1;
                            break;
                        case 6:
                            advantage += 1;
                            success += 1;
                            break;
                        case 7:
                            advantage += 1;
                            success += 1;
                            break;
                        case 8:
                            advantage += 1;
                            success += 1;
                            break;
                        case 9:
                            advantage += 2;
                        case 10:
                            success += 2;
                            break;
                        case 11:
                            success += 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case "s":
                    rolled = rng.ints(1,1,6).sum();
                    switch (rolled){
                        case 2:
                            advantage -= 1;
                            break;
                        case 3:
                            success -= 1;
                            break;
                        case 4:
                            success -= 1;
                            break;
                        case 5:
                            advantage -= 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case "d":
                    rolled = rng.ints(1,1,8).sum();
                    switch (rolled){
                        case 2:
                            advantage -= 1;
                            break;
                        case 3:
                            advantage -= 1;
                            success -= 1;
                            break;
                        case 4:
                            success -= 2;
                            break;
                        case 5:
                            advantage -= 1;
                            break;
                        case 6:
                            advantage -= 1;
                            break;
                        case 7:
                            advantage -= 2;
                            break;
                        case 8:
                            success -= 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case "c":
                    rolled = rng.ints(1,1,12).sum();
                    switch (rolled){
                        case 1:
                            success -= 1;
                            despair += 1;
                            break;
                        case 2:
                            advantage -= 1;
                            break;
                        case 3:
                            success -= 2;
                            break;
                        case 4:
                            success -= 1;
                            break;
                        case 5:
                            success -= 1;
                            advantage -= 1;
                            break;
                        case 6:
                            advantage -= 2;
                            break;
                        case 7:
                            advantage -= 2;
                            break;
                        case 8:
                            advantage -= 1;
                            success -= 1;
                            break;
                        case 9:
                            success -= 1;
                        case 10:
                            success -= 2;
                            break;
                        case 11:
                            advantage -= 1;
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
        return new int[]{success, advantage, triumph, despair};
    }

    private static void log(MessageCreateEvent event){
        System.out.println("-------------------------------------------------------------------");
        System.out.println(event.getMessageAuthor().getName());
        if(event.getServer().isPresent()){
            System.out.println(event.getChannel() + " | " + event.getServer().get().getName());
        } else {
            System.out.println(event.getChannel());
        }
        System.out.println(event.getMessageContent());
        System.out.println("-------------------------------------------------------------------");
    }

    private static void log(Exception e, MessageCreateEvent event){
        System.out.println("-------------------------------------------------------------------");
        System.out.println(event.getMessageAuthor().getName());
        if(event.getServer().isPresent()){
            System.out.println(event.getChannel() + " | " + event.getServer().get().getName());
        } else {
            System.out.println(event.getChannel());
        }
        System.out.println(event.getMessageContent() + " | Err: " + e.getMessage());
        System.out.println("-------------------------------------------------------------------");
    }
}
