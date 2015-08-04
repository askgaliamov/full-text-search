package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("/search")
    public List<Language> list(@RequestParam(value = "q", defaultValue = "") String query) {
        try {
            return searchService.search(query);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
