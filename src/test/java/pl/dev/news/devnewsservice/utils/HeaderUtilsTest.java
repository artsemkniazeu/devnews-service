package pl.dev.news.devnewsservice.utils;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import pl.dev.news.model.rest.RestUserModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeaderUtilsTest {

    @Test
    public void generatePaginationHeaders() {
        final List<RestUserModel> userModels = IntStream.range(0, 10)
                .mapToObj(i -> TestUtils.restUserModel())
                .collect(Collectors.toList());
        final Page<RestUserModel> page = new PageImpl<>(userModels, PageRequest.of(1, 2), userModels.size());
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders("baseurl/", page);
        final List<String> link = headers.get(HttpHeaders.LINK);
        final String expected = "<baseurl/?page=1&size=2>; rel=\"first\", <baseurl/?page=1&size=2>; rel=\"prev\", "
                + "<baseurl/?page=1&size=2>; rel=\"next\", <baseurl/?page=5&size=2>; rel=\"last\"";
        Assert.assertEquals(expected, link.get(0));
    }
}
