package owner.nycll152;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.base-url=https://example.test",
        "app.public-indexing-enabled=true",
        "app.admin.password=not-the-default-password"
})
@AutoConfigureMockMvc
class PublicIndexingSeoTests {

    private static final Path TEST_ROOT = initializeTestRoot();
    private static final Pattern JSON_LD_PATTERN = Pattern.compile(
            "<script type=\"application/ld\\+json\">\\s*(.*?)\\s*</script>",
            Pattern.DOTALL
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("app.data-root", () -> TEST_ROOT.resolve("data").toString());
        registry.add("app.storage-root", () -> TEST_ROOT.resolve("storage").toString());
    }

    @Test
    void publicPagesDropNoindexHeaderAndUseConfiguredCanonical() throws Exception {
        String html = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-Robots-Tag"))
                .andExpect(content().string(containsString("<link rel=\"canonical\" href=\"https://example.test/\">")))
                .andExpect(content().string(containsString("LL152 Guidance Desk")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> types = extractStructuredDataTypes(html);
        assertTrue(types.contains("Organization"));
        assertTrue(types.contains("WebSite"));
    }

    @Test
    void robotsListsSitemapWhenPublicIndexingIsEnabled() throws Exception {
        mockMvc.perform(get("/robots.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Disallow: /admin")))
                .andExpect(content().string(containsString("Sitemap: https://example.test/sitemap.xml")));
    }

    @Test
    void redirectsWrongPublicHostToConfiguredCanonicalDomain() throws Exception {
        mockMvc.perform(get("/filing-next-step/")
                        .queryParam("preview", "1")
                        .with(request -> {
                            request.setServerName("www.example.test");
                            request.setScheme("https");
                            request.setSecure(true);
                            return request;
                        }))
                .andExpect(status().isPermanentRedirect())
                .andExpect(header().string("Location", "https://example.test/filing-next-step/?preview=1"));
    }

    @Test
    void redirectsCanonicalHostToHttpsWhenRequestUsesHttp() throws Exception {
        mockMvc.perform(get("/ll152-checker/")
                        .with(request -> {
                            request.setServerName("example.test");
                            request.setScheme("http");
                            request.setSecure(false);
                            request.setServerPort(80);
                            return request;
                        }))
                .andExpect(status().isPermanentRedirect())
                .andExpect(header().string("Location", "https://example.test/ll152-checker/"));
    }

    @Test
    void sitemapListsIndexablePagesOnly() throws Exception {
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<loc>https://example.test/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/ll152-checker/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/filing-next-step/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/ll152-no-gas-piping-certification/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/ll152-no-active-gas-service/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/privacy-data-use/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/response-policy/</loc>")))
                .andExpect(content().string(containsString("<loc>https://example.test/terms-disclaimer/</loc>")))
                .andExpect(content().string(not(containsString("/corrected-certification/"))))
                .andExpect(content().string(not(containsString("/admin"))));
    }

    @Test
    void checkerEmitsBreadcrumbStructuredDataAndTeamTrust() throws Exception {
        String html = mockMvc.perform(get("/ll152-checker/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Current rule set")))
                .andExpect(content().string(containsString("LL152 Guidance Desk")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> types = extractStructuredDataTypes(html);
        assertTrue(types.contains("BreadcrumbList"));
        assertTrue(types.contains("WebPage"));
    }

    @Test
    void routeStructuredDataRemainsValidJson() throws Exception {
        String html = mockMvc.perform(get("/filing-next-step/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Current page basis")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> types = extractStructuredDataTypes(html);
        assertTrue(types.contains("BreadcrumbList"));
        assertTrue(types.contains("WebPage"));
    }

    @Test
    void privacyTrustRouteRendersFromInventory() throws Exception {
        mockMvc.perform(get("/privacy-data-use/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("How building details and contact data are handled")))
                .andExpect(content().string(containsString("What the intake stores")))
                .andExpect(content().string(containsString("Send case details")));
    }

    @Test
    void noGasRoutingPagesRenderFromInventory() throws Exception {
        mockMvc.perform(get("/ll152-no-gas-piping-certification/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("LL152 no gas piping certification")))
                .andExpect(content().string(containsString("When this route applies")));

        mockMvc.perform(get("/ll152-no-active-gas-service/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("LL152 no active gas service")))
                .andExpect(content().string(containsString("What DOB expects")));
    }

    private static Path initializeTestRoot() {
        try {
            Path tempRoot = Files.createTempDirectory("nycll152-public-indexing-test-");
            copyTree(Path.of("data"), tempRoot.resolve("data"));
            return tempRoot;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void copyTree(Path source, Path destination) throws IOException {
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path relative = source.relativize(path);
                Path target = destination.resolve(relative);
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(path, target);
                }
            }
        }
    }

    private List<String> extractStructuredDataTypes(String html) throws IOException {
        Matcher matcher = JSON_LD_PATTERN.matcher(html);
        List<String> types = new ArrayList<>();
        while (matcher.find()) {
            JsonNode node = objectMapper.readTree(matcher.group(1));
            if (node.isArray()) {
                for (JsonNode item : node) {
                    types.add(item.path("@type").asText());
                }
            } else {
                types.add(node.path("@type").asText());
            }
        }
        return types;
    }
}
