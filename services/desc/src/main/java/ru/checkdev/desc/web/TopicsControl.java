package ru.checkdev.desc.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.desc.domain.Topic;
import ru.checkdev.desc.dto.TopicDTO;
import ru.checkdev.desc.service.TopicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/topics")
@RestController
@AllArgsConstructor
public class TopicsControl {
    private final TopicService topicService;

    @GetMapping("/")
    public List<Topic> getAll() {
        return topicService.getAll();
    }

    @GetMapping("/{id}")
    public List<Topic> getByCategory(@PathVariable int id) {
        return topicService.findByCategory(id);
    }

    @GetMapping("/findCountsOfCategoryIdByTopicIds")
    public ResponseEntity<Map<Integer, Long>> getCountsOfCategoryIdByTopicIds(
            @RequestParam("topic_id") List<Integer> topicIds) {
        var value = getCategoryIdCounts(topicIds);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @GetMapping("/getByCategoryId/{categoryId}")
    public ResponseEntity<List<TopicDTO>> getByCategoryId(@PathVariable int categoryId) {
        return new ResponseEntity<>(topicService
                .getTopicDTOsByCategoryId(categoryId), HttpStatus.OK);
    }

    private Map<Integer, Long> getCategoryIdCounts(List<Integer> topicIds) {
        List<Topic> topics = new ArrayList<>();
        topicIds.forEach(tid -> topicService.findById(tid).ifPresent(topics::add));
        return topics.stream()
                .collect(
                        Collectors.groupingBy(t -> t.getCategory().getId(), Collectors.counting())
                );
    }
}
