package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * Servlet이 제공하는 Part는 파일 업로드 부분을 분간하는 코드가 필요하고
 * HttpServletRequest를 받아왔어야 했음
 *
 * 스프링은 MultipartFile이라는 인터페이스로 멀티파트 파일을 매우 편리하게 지원함
 * ** MultipartHttpServletRequest도 있지만 MultipartFile이  사용하기 더 편리하기 때문에 강의 생략
 *
 * [MultipartFile 주요 메서드]
 * file.getOriginalFilename(): 업로드 파일명
 * file.transferTo(new File(파일 경로)): 파일 저장
 */
@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {
    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file, //'@ModelAttribute'와도 사용 가능
                           HttpServletRequest request) throws IOException{
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if(!file.isEmpty()){
            String fullPath = fileDir+file.getOriginalFilename();
            log.info("파일 저장 fullPath={}",fullPath);
            file.transferTo(new File(fullPath));
        }

        return "upload-form";
    }
}
