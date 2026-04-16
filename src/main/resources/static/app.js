const leadDrawer = document.getElementById("leadDrawer");
const leadForm = document.getElementById("leadForm");
const leadFormStatus = document.getElementById("leadFormStatus");
const leadCloseButton = document.getElementById("leadCloseButton");
const leadDrawerTitle = document.getElementById("leadDrawerTitle");
const leadDrawerSummary = document.getElementById("leadDrawerSummary");
const leadSubmitButton = document.getElementById("leadSubmitButton");
const leadRouteId = document.getElementById("leadRouteId");
const leadRoutePath = document.getElementById("leadRoutePath");
const leadIntent = document.getElementById("leadIntent");
const leadScenarioKey = document.getElementById("leadScenarioKey");
const leadBuildingAddress = document.getElementById("leadBuildingAddress");
const leadPanel = leadDrawer?.querySelector(".lead-drawer__panel");
const checkerForm = document.getElementById("checkerForm");
const checkerStatus = document.getElementById("checkerStatus");
const checkerResult = document.getElementById("checkerResult");
const resultCoverage = document.getElementById("resultCoverage");
const resultDueCycle = document.getElementById("resultDueCycle");
const resultConfidence = document.getElementById("resultConfidence");
const resultConfidenceReason = document.getElementById("resultConfidenceReason");
const resultNextStep = document.getElementById("resultNextStep");
const resultSummary = document.getElementById("resultSummary");
const resultReviewBoundary = document.getElementById("resultReviewBoundary");
const resultChecklist = document.getElementById("resultChecklist");
const resultSources = document.getElementById("resultSources");
const resultRationale = document.getElementById("resultRationale");
const resultSourceNote = document.getElementById("resultSourceNote");
const resultRouteLink = document.getElementById("resultRouteLink");
const resultLeadButton = document.getElementById("resultLeadButton");
const gasPipingField = checkerForm?.querySelector("[name='gasPiping']");
const activeGasServiceField = checkerForm?.querySelector("[name='activeGasService']");
const modalFocusableSelector = [
    "button:not([disabled])",
    "[href]",
    "input:not([disabled])",
    "select:not([disabled])",
    "textarea:not([disabled])",
    "[tabindex]:not([tabindex='-1'])"
].join(", ");
const backgroundElements = leadDrawer
    ? Array.from(document.body.children).filter((element) => element !== leadDrawer)
    : [];
const defaultLeadCopy = {
    title: "Send case details",
    summary: "Use this when one missing fact still changes the filing path, due window, or gas-service branch. Sending the case now keeps the building facts, route, and blocker together before the wrong filing step creates delay.",
    button: "Send case details"
};
let lastFocusedElement = null;

function normalizeValue(value) {
    return value === "" ? null : value;
}

function buildingReference() {
    const address = document.getElementById("checkerAddress")?.value?.trim();
    if (address) {
        return address;
    }

    const bin = checkerForm?.querySelector("[name='bin']")?.value?.trim();
    if (bin) {
        return `BIN ${bin}`;
    }

    const bbl = checkerForm?.querySelector("[name='bbl']")?.value?.trim();
    if (bbl) {
        return `BBL ${bbl}`;
    }

    return "";
}

function syncActiveGasServiceField() {
    if (!activeGasServiceField) {
        return;
    }

    const enabled = gasPipingField?.value === "true";
    activeGasServiceField.disabled = !enabled;
    if (!enabled) {
        activeGasServiceField.value = "";
    }
}

async function responseMessage(response, fallbackMessage) {
    try {
        const payload = await response.json();
        if (typeof payload.message === "string" && payload.message.trim() !== "") {
            return payload.message;
        }
    } catch {
        // Ignore JSON parse errors and use the fallback message.
    }

    return fallbackMessage;
}

function focusableLeadElements() {
    if (!leadDrawer) {
        return [];
    }

    return Array.from(leadDrawer.querySelectorAll(modalFocusableSelector))
        .filter((element) => element instanceof HTMLElement)
        .filter((element) => !element.hasAttribute("disabled"))
        .filter((element) => element.offsetParent !== null || element === document.activeElement);
}

function setBackgroundInert(isInert) {
    backgroundElements.forEach((element) => {
        if (isInert) {
            element.setAttribute("inert", "");
        } else {
            element.removeAttribute("inert");
        }
    });
}

function focusLeadEntry() {
    const primaryField = leadForm?.querySelector("input[name='name']");
    if (primaryField instanceof HTMLElement) {
        primaryField.focus();
        return;
    }

    if (leadCloseButton instanceof HTMLElement) {
        leadCloseButton.focus();
        return;
    }

    if (leadPanel instanceof HTMLElement) {
        leadPanel.focus();
    }
}

function leadSummaryForIntent(intent) {
    switch (intent) {
        case "certification_help":
            return "Use this when the building may be on the no-gas-piping path and the remaining question is whether that certification route truly applies.";
        case "lmp_help":
            return "Use this when the building likely needs inspection and the remaining question is the branch, timing, or next filing step before scheduling moves forward.";
        default:
            return defaultLeadCopy.summary;
    }
}

function applyLeadCopy(options = {}) {
    const title = options.title || defaultLeadCopy.title;
    const summary = options.summary || defaultLeadCopy.summary;
    const button = options.button || defaultLeadCopy.button;

    if (leadDrawerTitle) {
        leadDrawerTitle.textContent = title;
    }

    if (leadDrawerSummary) {
        leadDrawerSummary.textContent = summary;
    }

    if (leadSubmitButton) {
        leadSubmitButton.textContent = button;
    }
}

function openLeadDrawer(options = {}) {
    if (!leadDrawer) {
        return;
    }

    if (document.activeElement instanceof HTMLElement) {
        lastFocusedElement = document.activeElement;
    }

    leadDrawer.classList.add("is-open");
    leadDrawer.setAttribute("aria-hidden", "false");
    document.body.classList.add("body--modal-open");
    setBackgroundInert(true);
    leadRouteId.value = options.routeId || "";
    leadRoutePath.value = options.routePath || window.location.pathname;
    leadIntent.value = options.intent || "filing_help";
    leadScenarioKey.value = options.scenarioKey || "";
    leadFormStatus.textContent = "";
    applyLeadCopy({
        title: options.label || defaultLeadCopy.title,
        summary: leadSummaryForIntent(options.intent),
        button: options.label || defaultLeadCopy.button
    });

    if (options.buildingAddress) {
        leadBuildingAddress.value = options.buildingAddress;
    }

    window.requestAnimationFrame(focusLeadEntry);

    postEvent({
        routeId: leadRouteId.value || null,
        routePath: leadRoutePath.value,
        eventType: "cta_click",
        scenarioKey: leadScenarioKey.value || null,
        detail: "lead_drawer_opened"
    });
}

function closeLeadDrawer() {
    if (!leadDrawer) {
        return;
    }

    leadDrawer.classList.remove("is-open");
    leadDrawer.setAttribute("aria-hidden", "true");
    document.body.classList.remove("body--modal-open");
    setBackgroundInert(false);
    applyLeadCopy(defaultLeadCopy);

    if (lastFocusedElement instanceof HTMLElement && document.contains(lastFocusedElement)) {
        lastFocusedElement.focus();
    }
}

async function postEvent(payload) {
    try {
        await fetch("/api/leads/event", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
    } catch (error) {
        console.error(error);
    }
}

document.querySelectorAll("[data-lead-open]").forEach((button) => {
    button.addEventListener("click", () => {
        openLeadDrawer({
            routeId: button.dataset.routeId || "",
            routePath: button.dataset.routePath || window.location.pathname,
            intent: button.dataset.intent || "filing_help",
            scenarioKey: button.dataset.scenarioKey || "",
            buildingAddress: buildingReference(),
            label: button.textContent?.trim()
        });
    });
});

leadCloseButton?.addEventListener("click", closeLeadDrawer);
leadDrawer?.addEventListener("click", (event) => {
    if (event.target === leadDrawer) {
        closeLeadDrawer();
    }
});
leadDrawer?.addEventListener("keydown", (event) => {
    if (!leadDrawer.classList.contains("is-open")) {
        return;
    }

    if (event.key === "Escape") {
        event.preventDefault();
        closeLeadDrawer();
        return;
    }

    if (event.key !== "Tab") {
        return;
    }

    const focusableElements = focusableLeadElements();
    if (focusableElements.length === 0) {
        event.preventDefault();
        if (leadPanel instanceof HTMLElement) {
            leadPanel.focus();
        }
        return;
    }

    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    if (event.shiftKey && document.activeElement === firstElement) {
        event.preventDefault();
        lastElement.focus();
        return;
    }

    if (!event.shiftKey && document.activeElement === lastElement) {
        event.preventDefault();
        firstElement.focus();
    }
});

leadForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    leadFormStatus.textContent = "Sending details...";

    const formData = new FormData(leadForm);
    const payload = Object.fromEntries(formData.entries());

    try {
        const response = await fetch("/api/leads/capture", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(await responseMessage(response, "Lead capture failed."));
        }

        leadFormStatus.textContent = "Case details sent. The route, blocker, and contact details are now captured together.";
        leadForm.reset();
        window.setTimeout(() => {
            closeLeadDrawer();
        }, 900);
    } catch (error) {
        leadFormStatus.textContent = error.message;
        console.error(error);
    }
});

syncActiveGasServiceField();
gasPipingField?.addEventListener("change", syncActiveGasServiceField);

checkerForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
        checkerStatus.textContent = "Running checker...";

    const formData = new FormData(checkerForm);
    const buildingType = normalizeValue(formData.get("buildingType"));
    const payload = {
        address: normalizeValue(formData.get("address")),
        bin: normalizeValue(formData.get("bin")),
        bbl: normalizeValue(formData.get("bbl")),
        dofClass: normalizeValue(formData.get("dofClass")),
        buildingType: buildingType === "unknown" ? null : buildingType,
        communityDistrict: normalizeValue(formData.get("communityDistrict")) ? Number(formData.get("communityDistrict")) : null,
        gasPiping: normalizeValue(formData.get("gasPiping")) === null ? null : formData.get("gasPiping") === "true",
        activeGasService: normalizeValue(formData.get("activeGasService")) === null ? null : formData.get("activeGasService") === "true"
    };

    const hasRoutingDetail = payload.communityDistrict !== null
        || payload.dofClass !== null
        || payload.buildingType !== null
        || payload.gasPiping !== null
        || payload.activeGasService !== null;

    if (!hasRoutingDetail) {
        checkerStatus.textContent = "Add a district, DOF class, building profile, or gas-status detail before you run the verdict.";
        return;
    }

    try {
        const response = await fetch("/api/checker/run", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(await responseMessage(response, "Checker request failed."));
        }

        const result = await response.json();
        checkerStatus.textContent = "Likely route ready.";
        checkerResult.hidden = false;
        resultCoverage.textContent = result.coverageVerdict;
        resultDueCycle.textContent = result.dueCycleVerdict;
        resultConfidence.textContent = result.confidenceLabel;
        resultConfidenceReason.textContent = result.confidenceReason;
        resultNextStep.textContent = result.nextStepTitle;
        resultSummary.textContent = result.nextStepSummary;
        resultReviewBoundary.textContent = result.reviewBoundary;
        resultSourceNote.textContent = result.sourceNote;
        resultRouteLink.href = result.recommendedRoute;
        resultRouteLink.textContent = "Open the recommended route";
        resultLeadButton.textContent = result.primaryCtaLabel;
        resultLeadButton.dataset.intent = result.primaryCtaIntent;
        resultLeadButton.dataset.routePath = result.recommendedRoute;
        resultLeadButton.dataset.routeId = result.recommendedRoute.replaceAll("/", "");
        resultLeadButton.dataset.scenarioKey = result.scenarioKey;

        resultChecklist.replaceChildren();
        result.nextActionChecklist.forEach((line) => {
            const item = document.createElement("li");
            item.textContent = line;
            resultChecklist.appendChild(item);
        });

        resultSources.replaceChildren();
        result.officialSources.forEach((source) => {
            const item = document.createElement("li");
            const link = document.createElement("a");
            link.href = source.url;
            link.target = "_blank";
            link.rel = "noopener";
            link.textContent = source.title;
            item.appendChild(link);
            resultSources.appendChild(item);
        });

        resultRationale.replaceChildren();
        result.rationale.forEach((line) => {
            const item = document.createElement("li");
            item.textContent = line;
            resultRationale.appendChild(item);
        });

        const reference = buildingReference();
        if (reference) {
            leadBuildingAddress.value = reference;
        }
    } catch (error) {
        checkerStatus.textContent = error.message;
        console.error(error);
    }
});
