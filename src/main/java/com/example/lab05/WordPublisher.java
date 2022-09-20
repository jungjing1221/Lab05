package com.example.lab05;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class WordPublisher {
    protected Word words = new Word();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/addBad/{word}", method = RequestMethod.GET)
    public ArrayList<String> addBadWord(
            @PathVariable("word") String s
    ){
        words.badWords.add(s);
        return words.badWords;
    }

    @RequestMapping(value = "/delBad/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteBadWord(
            @PathVariable("word") String s
    ){
        words.badWords.remove(String.valueOf(s));
        return words.badWords;
    }

    @RequestMapping(value = "/addGood/{word}", method = RequestMethod.GET)
    public ArrayList<String> addGoodWord(
            @PathVariable("word") String s
    ){
        words.goodWords.add(s);
        return words.goodWords;
    }

    @RequestMapping(value = "/delGood/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteGoodWord(
            @PathVariable("word") String s
    ){
        words.goodWords.remove(String.valueOf(s));
        return words.goodWords;
    }

    @RequestMapping(value = "/proof/{sentence}", method = RequestMethod.GET)
    public String  proofSentence(
            @PathVariable("sentence") String s
    ){
        String msg = "";
        String count = "";
        for(String i : words.goodWords){
            if(s.indexOf(i) !=-1){
                count+="g";
                break;
            }
        }

        for(String i : words.badWords){
            if(s.indexOf(i) !=-1){
                count+="b";
                break;
            }
        }

        if(count.equals("g")){
            rabbitTemplate.convertAndSend("Direct", "gWord", s);
            msg += "Found Good Word";
        } else if (count.equals("b")) {
            rabbitTemplate.convertAndSend("Direct", "bWord", s);
            msg += "Found Bad Word";
        } else if (count.equals("gb")) {
            rabbitTemplate.convertAndSend("Fanout", "", s);
            msg += "Found Good Word & Found Bad Word";
        }
        return msg;
    }
}