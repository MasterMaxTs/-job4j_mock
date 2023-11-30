package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.dto.TopicLiteDTO;
import ru.job4j.site.dto.TopicIdNameDTO;

import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicsService {

    private final RestAuthCall restAuthCall;

    public List<TopicDTO> getByCategory(int id) throws JsonProcessingException {
        restAuthCall.setUrl("http://localhost:9902/topics/" + id);
        var text = restAuthCall.get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO getById(int id) {
        try {
            restAuthCall.setUrl("http://localhost:9902/topic/" + id);
            var text = restAuthCall.get();
            var mapper = new ObjectMapper();
            return mapper.readValue(text, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public TopicDTO create(String token, TopicLiteDTO topicLite) throws JsonProcessingException {
        restAuthCall.setUrl("http://localhost:9902/topic/");
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setName(topicLite.getName());
        topic.setPosition(topicLite.getPosition());
        topic.setText(topicLite.getText());
        var category = new CategoryDTO();
        category.setId(topicLite.getCategoryId());
        topic.setCategory(category);
        var out = restAuthCall.post(token, mapper.writeValueAsString(topic));
        return mapper.readValue(out, TopicDTO.class);
    }

    public void update(String token, TopicDTO topic) throws JsonProcessingException {
        restAuthCall.setUrl("http://localhost:9902/topic/");
        var mapper = new ObjectMapper();
        topic.setUpdated(Calendar.getInstance());
        var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(topic);
        restAuthCall.update(token, json);
    }

    public void delete(String token, int id) throws JsonProcessingException {
        restAuthCall.setUrl("http://localhost:9902/topic/");
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setId(id);
        restAuthCall.delete(token, mapper.writeValueAsString(topic));
    }

    public String getNameById(int id) {
        restAuthCall.setUrl(String.format("http://localhost:9902/topic/name/%d", id));
        return restAuthCall.get();
    }

    public List<TopicIdNameDTO> getTopicIdNameDtoByCategory(int categoryId)
            throws JsonProcessingException {
        restAuthCall.setUrl(
                String.format("http://localhost:9902/topics/getByCategoryId/%d", categoryId));
        var text = restAuthCall.get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }
}
