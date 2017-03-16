package com.simplereverseproxy.filters;

/**
 * Created by felipe.amaral on 20/02/2017.
 */

import java.util.Optional;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class GatewayPostFilter extends ZuulFilter {

    private static final String LOCATION = "Location";
    private static Logger log = LoggerFactory.getLogger(GatewayPostFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return getLocationHeader(RequestContext.getCurrentContext()) != null;
    }

    @Override
    public Object run() {
        Optional<Pair<String, String>> header = getLocationHeader(RequestContext.getCurrentContext());
        header.ifPresent(x -> rewriteLocation(x));
        return null;
    }

    private void rewriteLocation(Pair<String, String> header) {

        String resultLocation = header.second().replaceAll("^http[s]?:\\/\\/.*?\\/",
                                                           ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/");
        log.info(String.format("Rewriting Location %s to %s ", header.second(), resultLocation));
        header.setSecond(resultLocation);
    }

    private Optional<Pair<String, String>> getLocationHeader(RequestContext ctx) {
        return ctx.getZuulResponseHeaders().stream()
                  .filter(x -> LOCATION.contains(x.first()))
                  .findFirst();
    }
}