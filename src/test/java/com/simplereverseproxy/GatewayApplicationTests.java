package com.simplereverseproxy;

import com.netflix.zuul.context.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class GatewayApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RouteLocator routes;

    private MockServerClient mockServerClient;

    private String getRoute(String path) {
        return this.routes.getMatchingRoute(path).getLocation();
    }

    @Before
    public void setTestRequestContext() {
        RequestContext context = new RequestContext();
        RequestContext.testSetCurrentContext(context);
    }

    @After
    public void stopProxy() {
        if (mockServerClient != null) {
            mockServerClient.stop();
        }
    }

    @Test
    public void bindRoute() {
        assertNotNull(getRoute("/app1/**"));
        assertNotNull(getRoute("/app2/**"));
    }

    @Test
    public void testForwardedPatterRyota() {
        startApp1Server();
        ResponseEntity<String> result = testRestTemplate.getForEntity("http://localhost:" + port + "app1", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testForwardedPatterClientPlatform() {
        startApp2Server();
        ResponseEntity<String> result = testRestTemplate.getForEntity("http://localhost:" + port + "/app2", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    private void startApp1Server() {
        mockServerClient = startClientAndServer(9999);
        mockServerClient.when(request()
                              .withMethod("GET")
                              .withPath("/app1/"))
                        .respond(response()
                                 .withStatusCode(200));
    }

    private void startApp2Server() {
        mockServerClient = startClientAndServer(9998);
        mockServerClient.when(request()
                              .withMethod("GET")
                              .withPath("/app2/"))
                        .respond(response()
                                 .withStatusCode(200));
    }

}