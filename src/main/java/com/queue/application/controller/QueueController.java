package com.queue.application.controller;

import com.queue.application.domain.DefaultQueue;
import com.queue.application.domain.QueueData;
import com.queue.application.service.QueueServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/queue")
@Slf4j
public class QueueController {
    public QueueServices queueServices;

    @Autowired
    MessageSource messageSource;

    @Autowired
    public QueueController(QueueServices queueServices){
        this.queueServices = queueServices;
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> saveMessage(@RequestBody  DefaultQueue defaultQueue){
        return new ResponseEntity<>(queueServices.createQueue(defaultQueue), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/listusers",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> listOfQueues(){
        return new ResponseEntity<>(queueServices.getMessage(),HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{userId}",method = RequestMethod.DELETE)
    ResponseEntity<?> deleteuser(@PathVariable("userId") String UserId){
            return new ResponseEntity<>(queueServices.DeleteUserQueue(UserId),HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteAll",method = RequestMethod.DELETE)
    public void deleteAll(){
        queueServices.DeleteAllQueues();
    }

    @RequestMapping(value = "/message/{userId}",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> CreateMessage(@RequestBody QueueData queueData,@PathVariable("userId") String userId){
       return new ResponseEntity<>(queueServices.AddNewMessage(queueData,userId),HttpStatus.CREATED);
    }

    @RequestMapping(value = "/message",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> CreateMessageForDefaultUser(@RequestBody QueueData queueData){
        //System.out.println(queueData.getMessage());
        return new ResponseEntity<>(queueServices.AddNewMessage(queueData,"default"),HttpStatus.CREATED);
    }

    @RequestMapping(value = "/peek/{userId}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFirstMessage(@PathVariable("userId") String userId){
       return new ResponseEntity<>(queueServices.getFirstmessage(userId),HttpStatus.OK);
    }

    @RequestMapping(value = "/peek",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDefaultQueueFirstMessage(){
        return new ResponseEntity<>(queueServices.getFirstmessage("default"),HttpStatus.OK);
    }

    @RequestMapping(value = "/poll",method = RequestMethod.DELETE)
    public ResponseEntity<?> pollDefaultQueue(){
         return  new ResponseEntity<>(queueServices.PollQueue("default"),HttpStatus.OK);
    }

    @RequestMapping(value = "/poll/{userId}",method = RequestMethod.DELETE)
    public ResponseEntity<?> pollQueue(@PathVariable("userId") String userId){
            return  new ResponseEntity<>(queueServices.PollQueue(userId),HttpStatus.OK);
    }

    @RequestMapping(value = "/list/{userId}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMessage(@PathVariable("userId") String userId){
       return new ResponseEntity<>(queueServices.getAllMessages(userId),HttpStatus.OK);
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllMessagesFormDefaultQueue(){
        return new ResponseEntity<>(queueServices.getAllMessages("default"),HttpStatus.OK);
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String internationalization(Locale locale, Model model){
        LOGGER.info("Welcome to Queue ............ {} {}",messageSource.getMessage("greeting",null, LocaleContextHolder.getLocale()),LocaleContextHolder.getLocale());
        return "lang.change";
    }
}
