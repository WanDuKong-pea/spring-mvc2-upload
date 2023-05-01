package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor //Lombok 라이브러리에서 제공하는 애노테이션
                        //final 키워드가 붙은 필드를 가지고 생성자를 자동으로 생성
public class ItemController {
    private final ItemRepository itemRepository; //item 저장 업무 처리
    private final FileStore fileStore; //파일 저장과 관련된 업무 처리

    /**
     * 단순 아이템 등록 폼 반환 메서드
     */
    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        //object, field 사용하기 위해 @ModelAttribute 사용
        return "item-form";
    }

    /**
     * 폼에서 넘어온 아이템과 파일 저장 메서드
     */
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        //서버에 파일 저장하고 서버파일명, 클라이언트 파일명 정보를 가진 UploadFile 반환됨.
        //@ModelAttribute를 통해 ItemForm의 MultipartFile attachFile 에 바인딩되어 들어옴
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());

        //서버에 파일들을 저장하고 서버파일명, 클라이언트 파일명 정보를 가진 List<UploadFile> 반환됨
        //@ModelAttribute를 통해 ItemForm의 List<MultipartFile> imageFiles 에 바인딩되어 들어옴
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        //데이터 베이스에 저장
        //item 객체 생성 + 값 입력
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        //저장
        itemRepository.save(item);

        //itemId를 redirectAttribute 에 담아서 /items/{itemId} 컨트롤러에 전달
        redirectAttributes.addAttribute("itemId", item.getId());
        return "redirect:/items/{itemId}";
    }

    /**
     * 아이템 아이디별 상세 보기 페이지로 이동시켜줌
     */
    @GetMapping("items/{id}")
    public String items(@PathVariable Long id, Model model) {

        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    /**
     * 태그로 이미지를 조회할때 사용
     * UrlResource로 이미지 파일을 읽어서 @ResponseBody로 이미지 바이너리 변환
     * 서버에 저장된 파일명을 파라미터로 받음
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    /**
     * 파일을 다운로드 할 때 실행. 예제를 더 단순화 가능하지만
     * 파일 다운로드 시 권한 체크같은 복잡한 상황까지 가정한다 생각하고 이미지id를 요청하도록 함
     * 파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋음.
     * 이때는 Content-Disposition 헤더에 attachment; filename="업로드 파일명" 값을 주면 됨
     */
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        //DB에서 itemId에 해당하는 아이템 조회
        Item item = itemRepository.findById(itemId);

        //아이템에서 UploadFile를 받아 서버저장 파일명, 업로드된 파일명을 받아옴
        String storeFilename = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        //UrlResource로 이미지 파일을 읽어옴
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFilename));

        log.info("uploadFileName={}", uploadFileName);

        //업로드된 파일명을 UTF_8로 인코딩
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        //Content-Dispostion 헤더에 쓰기 위함
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";


        return ResponseEntity.ok() //200 OK 반환
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) //다운로드시 파일 이름 설정
                .body(resource); //다운로드할 파일 데이터 설정
    }
}
