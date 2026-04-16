package owner.nycll152;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Nycll152ApplicationTests {

	private static final Path TEST_ROOT = initializeTestRoot();

	@Autowired
	private MockMvc mockMvc;

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("app.data-root", () -> TEST_ROOT.resolve("data").toString());
		registry.add("app.storage-root", () -> TEST_ROOT.resolve("storage").toString());
	}

	@Test
	void contextLoads() {
	}

	@Test
	void corePagesRender() throws Exception {
		for (String path : List.of(
				"/",
				"/ll152-checker/",
				"/filing-next-step/",
				"/no-gas-vs-no-active-gas-service/",
				"/ll152-no-gas-piping-certification/",
				"/ll152-no-active-gas-service/",
				"/extension-penalty-waiver/",
				"/after-inspection-gps1-gps2/",
				"/2026-deadline/",
				"/about/",
				"/methodology/",
				"/contact/",
				"/not-government-affiliated/"
		)) {
			mockMvc.perform(get(path))
					.andExpect(status().isOk());
		}
	}

	@Test
	void homeHighlightsBlockerFirstEntry() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Does this building need LL152 inspection?")));
	}

	@Test
	void adminRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/admin"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminRendersWithBasicAuth() throws Exception {
		mockMvc.perform(get("/admin").with(httpBasic("admin", "change-me-before-deploy")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Admin summary")));
	}

	@Test
	void adminShowsRecentLeadAndEventRows() throws Exception {
		mockMvc.perform(post("/api/leads/event")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "routePath": "/filing-next-step/",
								  "eventType": "cta_click",
								  "scenarioKey": "active_gas_service",
								  "detail": "lead_drawer_opened"
								}
								"""))
				.andExpect(status().isAccepted());

		mockMvc.perform(post("/api/leads/capture")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "Admin Review",
								  "email": "admin-review@example.com",
								  "phone": "555-0110",
								  "buildingAddress": "44 Beaver Street",
								  "routePath": "/filing-next-step/",
								  "intent": "filing_help",
								  "scenarioKey": "active_gas_service",
								  "message": "Need route confirmation."
								}
								"""))
				.andExpect(status().isOk());

		mockMvc.perform(get("/admin").with(httpBasic("admin", "change-me-before-deploy")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Latest review requests")))
				.andExpect(content().string(containsString("admin-review@example.com")))
				.andExpect(content().string(containsString("44 Beaver Street")))
				.andExpect(content().string(containsString("Latest CTA and intake activity")))
				.andExpect(content().string(containsString("lead_drawer_opened")))
				.andExpect(content().string(containsString("/admin/download/leads.csv")))
				.andExpect(content().string(containsString("/admin/download/admin-metrics-snapshot.json")));
	}

	@Test
	void adminExportsRequireAuthAndReturnFiles() throws Exception {
		mockMvc.perform(get("/admin/download/leads.csv"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/admin/download/lead-events.csv").with(httpBasic("admin", "change-me-before-deploy")))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", containsString("lead-events.csv")))
				.andExpect(content().string(containsString("event_id")));

		mockMvc.perform(get("/admin/download/admin-metrics-snapshot.json").with(httpBasic("admin", "change-me-before-deploy")))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", containsString("admin-metrics-snapshot.json")))
				.andExpect(content().string(containsString("\"ctaClicks\"")));
	}

	@Test
	void indexingIsLockedByDefault() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(header().string("X-Robots-Tag", "noindex, nofollow"))
				.andExpect(header().string("Content-Security-Policy", containsString("script-src 'self' https://static.cloudflareinsights.com")))
				.andExpect(header().string("Content-Security-Policy", containsString("connect-src 'self' https://cloudflareinsights.com")));

		mockMvc.perform(get("/robots.txt"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Disallow: /")));
	}

	@Test
	void heldRoutesEmitNoindex() throws Exception {
		mockMvc.perform(get("/corrected-certification/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("noindex,follow")));
	}

	@Test
	void checkerApiReturnsVerdict() throws Exception {
		mockMvc.perform(post("/api/checker/run")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "address": "123 Sample Street",
								  "buildingType": "multifamily",
								  "communityDistrict": 6,
								  "gasPiping": true,
								  "activeGasService": true
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.recommendedRoute").value("/filing-next-step/"))
				.andExpect(jsonPath("$.primaryCtaIntent").value("lmp_help"))
				.andExpect(jsonPath("$.dueCycleVerdict").value(org.hamcrest.Matchers.containsString("Sub-cycle C")))
				.andExpect(jsonPath("$.confidenceLabel").value(org.hamcrest.Matchers.containsString("High confidence")))
				.andExpect(jsonPath("$.confidenceReason").value(org.hamcrest.Matchers.containsString("active-gas branch")))
				.andExpect(jsonPath("$.reviewBoundary").value(org.hamcrest.Matchers.containsString("GPS1")))
				.andExpect(jsonPath("$.officialSources[0].title").value("Periodic Gas Piping System Inspections"));
	}

	@Test
    void checkerApiUsesExemptDofClass() throws Exception {
        mockMvc.perform(post("/api/checker/run")
                        .contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "address": "45 Example Avenue",
								  "dofClass": "A6",
								  "buildingType": "one_or_two_family",
								  "communityDistrict": 6,
								  "gasPiping": false
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.recommendedRoute").value("/exempt-building-notification/"))
                .andExpect(jsonPath("$.coverageVerdict").value(org.hamcrest.Matchers.containsString("Likely not covered")))
                .andExpect(jsonPath("$.nextActionChecklist[0]").exists());
    }

    @Test
    void checkerRejectsLocationOnlyRequests() throws Exception {
        mockMvc.perform(post("/api/checker/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "123 Sample Street"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Add a district, DOF class, building profile, or gas-status detail before you run the verdict."));
    }

    @Test
    void checkerRejectsContradictoryGasStatusInputs() throws Exception {
        mockMvc.perform(post("/api/checker/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "address": "123 Sample Street",
                                  "communityDistrict": 6,
                                  "gasPiping": false,
                                  "activeGasService": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Active gas service can only be set after you confirm gas piping is present."));
    }

    @Test
    void routePageShowsOfficialSourceAnchors() throws Exception {
        mockMvc.perform(get("/filing-next-step/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Official city references")));
	}

	@Test
	void leadCaptureWritesStorage() throws Exception {
		mockMvc.perform(post("/api/leads/capture")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "Owner Example",
								  "email": "owner@example.com",
								  "phone": "555-0100",
								  "buildingAddress": "123 Sample Street",
								  "routePath": "/filing-next-step/",
								  "intent": "filing_help",
								  "scenarioKey": "active_gas_service",
								  "message": "Need help with the next filing step."
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("captured"));

		String leadsFile = Files.readString(TEST_ROOT.resolve("storage").resolve("leads").resolve("leads.csv"));
		assertThat(leadsFile).contains("owner@example.com");
	}

	@Test
	void leadCaptureSanitizesCsvFormulaPayloads() throws Exception {
		mockMvc.perform(post("/api/leads/capture")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "=Owner Formula",
								  "email": "formula@example.com",
								  "phone": "555-0100",
								  "buildingAddress": "123 Sample Street",
								  "routePath": "/filing-next-step/",
								  "intent": "filing_help",
								  "message": "@spreadsheet"
								}
								"""))
				.andExpect(status().isOk());

        String leadsFile = Files.readString(TEST_ROOT.resolve("storage").resolve("leads").resolve("leads.csv"));
        assertThat(leadsFile).contains("'=Owner Formula");
        assertThat(leadsFile).contains("'@spreadsheet");
    }

    @Test
    void leadCaptureIgnoresClientSuppliedRouteId() throws Exception {
        mockMvc.perform(post("/api/leads/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Owner Example",
                                  "email": "trusted-route@example.com",
                                  "buildingAddress": "123 Sample Street",
                                  "routeId": "poisoned-route",
                                  "routePath": "/filing-next-step/",
                                  "intent": "filing_help"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("captured"));

        String routeStatusFile = Files.readString(TEST_ROOT.resolve("data").resolve("ops").resolve("route-status.csv"));
        assertThat(routeStatusFile).doesNotContain("poisoned-route");
    }

    @Test
    void leadCaptureRejectsUnknownRoutePaths() throws Exception {
        mockMvc.perform(post("/api/leads/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Owner Example",
                                  "email": "unknown-route@example.com",
                                  "buildingAddress": "123 Sample Street",
                                  "routePath": "/totally-unknown/",
                                  "intent": "filing_help"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", equalTo("Unknown route path.")));
    }

    @Test
    void rateLimitUsesForwardedClientIpWhenPresent() throws Exception {
        String requestBody = """
                {
                  "address": "123 Sample Street",
                  "communityDistrict": 6
                }
                """;

        for (int index = 0; index < 60; index++) {
            mockMvc.perform(post("/api/checker/run")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Forwarded-For", "198.51.100.10")
                            .content(requestBody))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/checker/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", "198.51.100.11")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

	private static Path initializeTestRoot() {
		try {
			Path tempRoot = Files.createTempDirectory("nycll152-test-");
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
}
