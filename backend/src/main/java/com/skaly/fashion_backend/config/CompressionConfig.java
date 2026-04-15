package com.skaly.fashion_backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

@Configuration
public class CompressionConfig {

    @Bean
    public FilterRegistrationBean<GzipFilter> gzipFilter() {
        FilterRegistrationBean<GzipFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new GzipFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/api/*"));
        return registrationBean;
    }

    public static class GzipFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            
            String acceptEncoding = request.getHeader("Accept-Encoding");
            
            if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
                response.setHeader("Content-Encoding", "gzip");
                GzipResponseWrapper wrappedResponse = new GzipResponseWrapper(response);
                try {
                    filterChain.doFilter(request, wrappedResponse);
                    wrappedResponse.finish();
                } finally {
                    // Ensure the response is properly closed
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    public static class GzipResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        private GZIPOutputStream gzipOutputStream;
        private jakarta.servlet.ServletOutputStream servletOutputStream;

        public GzipResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
            response.setHeader("Content-Encoding", "gzip");
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
            if (servletOutputStream == null) {
                if (gzipOutputStream == null) {
                    gzipOutputStream = new GZIPOutputStream(getResponse().getOutputStream());
                }
                servletOutputStream = new jakarta.servlet.ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        gzipOutputStream.write(b);
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        gzipOutputStream.write(b);
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        gzipOutputStream.write(b, off, len);
                    }

                    @Override
                    public void flush() throws IOException {
                        gzipOutputStream.flush();
                    }

                    @Override
                    public void close() throws IOException {
                        gzipOutputStream.close();
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                        // Not implemented
                    }
                };
            }
            return servletOutputStream;
        }

        public void finish() throws IOException {
            if (gzipOutputStream != null) {
                gzipOutputStream.finish();
            }
        }
    }
}
