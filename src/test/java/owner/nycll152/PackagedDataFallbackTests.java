package owner.nycll152;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import owner.nycll152.config.AppDataLocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.base-url=https://example.test",
        "app.public-indexing-enabled=false"
})
@AutoConfigureMockMvc
class PackagedDataFallbackTests {

    private static final Path STORAGE_ROOT = createStorageRoot();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppDataLocator appDataLocator;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("app.storage-root", () -> STORAGE_ROOT.toString());
    }

    @Test
    void packagedPagesRenderWithoutExternalDataRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Know the filing path before DOB turns into guesswork.")));

        mockMvc.perform(get("/ll152-checker/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Likely next step")));

        mockMvc.perform(get("/privacy-data-use/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("What the intake stores")));
    }

    @Test
    void packagedSeedFilesInitializeInsideStorageRoot() throws IOException {
        Path routeStatus = appDataLocator.ensureRouteStatusFile();
        Path promotionReview = appDataLocator.ensurePromotionReviewFile();

        assertThat(routeStatus).startsWith(STORAGE_ROOT.resolve("ops"));
        assertThat(Files.exists(routeStatus)).isTrue();
        assertThat(Files.readString(routeStatus)).contains("route id,route path");

        assertThat(promotionReview).startsWith(STORAGE_ROOT.resolve("ops"));
        assertThat(Files.exists(promotionReview)).isTrue();
        assertThat(Files.readString(promotionReview)).contains("\"heldRecommendations\"");
    }

    private static Path createStorageRoot() {
        try {
            return Files.createTempDirectory("nycll152-packaged-data-");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create packaged-data test storage root", exception);
        }
    }
}
