package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * 서블릿이 제공하는 'Part'와 'HttpServletRequest'를 이용하여 파일 저장을 해보자.
 */
@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {
    @Value("${file.dir}") //스프링 애노테이션 LOMBOK 아님
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFilev1(HttpServletRequest request) throws ServletException, IOException{
        log.info("request={}",request);

        String itemName = request.getParameter("itemName");
        log.info("itemName={}",itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts={}",parts);

        //request.getParts() 를 순회하면서 각 part 별 담긴 메시지 읽어보기
        for(Part part : parts){

            log.info("=== PART ===");
            log.info("name={}",part.getName());

            //각 'part'가 가진 Http Header 와 Header 정보를 읽어오기
            //각 'part'도 헤더와 바디가 있음
            Collection<String> headerNames = part.getHeaderNames();
            for(String headerName: headerNames){
                log.info("header {} : {}",headerName, part.getHeader(headerName));
            }

            //[편의 메서드]
            //part의 Content-Disposition: form-data; name="xxx"; filename="xxx.xxx"
            //part.getSubmittedFileName() : 클라이언트가 전달한 파일명
            log.info("submittedFileName={}",part.getSubmittedFileName());
            //multipart 각 part 의 사이즈
            log.info("part body size={}",part.getSize());

            //데이터 읽기
            //part.getInputStream(): part의 전송 데이터를 읽을 수 있음
            //각 'part'의 바디 정보를 읽어오기.
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body={}",body);

            //파일에 저장하기
            //클라이언트 전달 파일명이 NULL이 아니면 = 현재 파트가 클라이언트 전달 파일명을 포함하고 있으면 = 현재 파트가 파일이 담긴 파트라면
            if(StringUtils.hasText(part.getSubmittedFileName())){
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                //part.write(경로), part를 통해 전송된 데이터를 저장 가능
                part.write(fullPath);
            }
            //IOException이 계속 터져서 임시 방편으로 적어놓음
            inputStream.close();
        }
        return "upload-form";
    }
}
