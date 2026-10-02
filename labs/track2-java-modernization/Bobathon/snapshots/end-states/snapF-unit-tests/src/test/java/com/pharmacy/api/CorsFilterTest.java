package com.pharmacy.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorsFilterTest {

    private static final String ALLOW_ORIGIN_HEADER  = "Access-Control-Allow-Origin";
    private static final String ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    private static final String ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    private static final String MAX_AGE_HEADER       = "Access-Control-Max-Age";

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private ContainerResponseContext responseContext;

    private CorsFilter corsFilter;

    @BeforeEach
    void setUp() {
        corsFilter = new CorsFilter();
    }

    @Test
    void filter_called_addsAccessControlAllowOriginWildcard() throws IOException {
        // Arrange
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);

        // Act
        corsFilter.filter(requestContext, responseContext);

        // Assert
        assertThat(headers.getFirst(ALLOW_ORIGIN_HEADER)).isEqualTo("*");
    }

    @Test
    void filter_called_addsAccessControlAllowMethods() throws IOException {
        // Arrange
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);

        // Act
        corsFilter.filter(requestContext, responseContext);

        // Assert
        assertThat(headers.getFirst(ALLOW_METHODS_HEADER).toString())
                .contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @Test
    void filter_called_addsAccessControlAllowHeaders() throws IOException {
        // Arrange
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);

        // Act
        corsFilter.filter(requestContext, responseContext);

        // Assert
        assertThat(headers.getFirst(ALLOW_HEADERS_HEADER).toString())
                .contains("Content-Type", "Authorization");
    }

    @Test
    void filter_called_addsAccessControlMaxAge() throws IOException {
        // Arrange
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(responseContext.getHeaders()).thenReturn(headers);

        // Act
        corsFilter.filter(requestContext, responseContext);

        // Assert
        assertThat(headers.getFirst(MAX_AGE_HEADER)).isEqualTo("3600");
    }
}
