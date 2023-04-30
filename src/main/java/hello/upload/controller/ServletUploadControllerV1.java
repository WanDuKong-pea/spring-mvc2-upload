package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

/**
 * "form"에서 파일을 받아올떄 "Multipart"로 HTTP 요청이 들어오는 것을 확인해보자
 */
@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadControllerV1 {
    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException{
        log.info("request={}",request);
        String itemName = request.getParameter("itemName");
        log.info("itemName={}",itemName);
        //request.getParts(): multipart/form-data 전송 방식에서
                              //각각 나누어진 부분 모두 받아서 확인 가능
        Collection<Part> parts = request.getParts();
        log.info("parts={}",parts);

        //request 로그를 보면 HttpServletRequest 객체가 RequestFacade →
        //StandardMultipartHttpServletRequest로 변한 것을 확인 가능

        return "upload-form";
    }
}
