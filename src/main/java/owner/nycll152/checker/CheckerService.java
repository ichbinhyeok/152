package owner.nycll152.checker;

import owner.nycll152.sources.OfficialSourceLink;
import owner.nycll152.sources.SourceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CheckerService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.US);

    private final CheckerRulesRepository checkerRulesRepository;
    private final SourceService sourceService;

    public CheckerService(CheckerRulesRepository checkerRulesRepository, SourceService sourceService) {
        this.checkerRulesRepository = checkerRulesRepository;
        this.sourceService = sourceService;
    }

    public CheckerResult evaluate(CheckerRequest request) {
        CheckerRules rules = checkerRulesRepository.load();
        BuildingProfile buildingProfile = BuildingProfile.from(request.buildingType());
        String dofClass = normalizeDofClass(request.dofClass());
        boolean hasLocationHint = StringUtils.hasText(request.address())
                || StringUtils.hasText(request.bin())
                || StringUtils.hasText(request.bbl());
        boolean hasGasPipingAnswer = request.gasPiping() != null;
        boolean gasPiping = Boolean.TRUE.equals(request.gasPiping());
        boolean hasActiveGasAnswer = request.activeGasService() != null;
        boolean activeGasService = Boolean.TRUE.equals(request.activeGasService());
        boolean exemptDofClass = dofClass != null && rules.exemptDofClassifications().contains(dofClass);
        SubCycleWindow subCycleWindow = findSubCycleWindow(rules, request.communityDistrict());

        List<String> rationale = new ArrayList<>();
        rationale.add("This checker gives a likely verdict based on current DOB and NYC311 guidance verified on " + rules.verifiedAt() + ".");
        rationale.add("Always verify the exact filing obligation, deadline, and correction path before you submit anything to DOB.");
        if (hasLocationHint && dofClass == null && buildingProfile == BuildingProfile.UNKNOWN) {
            rationale.add("Address, BIN, and BBL help keep the building notes together, but this phase does not resolve DOB property records from them yet.");
        }

        String coverageVerdict = buildCoverageVerdict(buildingProfile, dofClass, exemptDofClass, rationale, rules);
        String dueCycleVerdict = buildDueCycleVerdict(request.communityDistrict(), subCycleWindow, rationale, rules);

        String nextStepTitle;
        String nextStepSummary;
        String recommendedRoute;
        String primaryCtaLabel;
        String primaryCtaIntent;
        String scenarioKey;
        List<String> nextActionChecklist;
        List<String> sourceIds;

        if (exemptDofClass || buildingProfile == BuildingProfile.ONE_OR_TWO_FAMILY) {
            nextStepTitle = "Verify the exempt-building record before you act on an LL152 notice.";
            nextStepSummary = "DOB treats specific DOF classes as exempt R-3 style properties. If the property profile is wrong, use the exempt-building route rather than assuming the notice is final.";
            recommendedRoute = "/exempt-building-notification/";
            primaryCtaLabel = "Send case details";
            primaryCtaIntent = "filing_help";
            scenarioKey = "possible_exempt_building";
            nextActionChecklist = List.of(
                    "Verify the DOF class, BIN, or BBL against the official property record before relying on the exemption answer.",
                    "Keep any notice, violation, or property-record mismatch ready before you challenge the filing path.",
                    "Use the exempt-building page before you pay or file against the wrong classification."
            );
            sourceIds = List.of("periodic-gas-piping-inspections", "cycle-2-service-notice");
            rationale.add("The official DOB page says one- and two-family homes and listed R-3 style DOF classes do not need to comply.");
        } else if (hasGasPipingAnswer && !gasPiping) {
            nextStepTitle = "Use the no-gas-piping filing path.";
            nextStepSummary = "DOB now treats no-gas-piping certification as a one-time submission, and the certification can come from a utility company, registered design professional, or licensed master plumber.";
            recommendedRoute = "/ll152-no-gas-piping-certification/";
            primaryCtaLabel = "Send case details";
            primaryCtaIntent = "certification_help";
            scenarioKey = "no_gas_piping";
            nextActionChecklist = List.of(
                    "Confirm there is no gas piping anywhere in the building before using this path.",
                    "Collect BIN or BBL plus the signer details for the utility, registered design professional, or LMP who will certify the condition.",
                    "Treat the certification as one-time unless gas piping is later installed."
            );
            sourceIds = List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "rcny-103-10");
            rationale.add("If DOB accepts the no-gas-piping certification, no further LL152 action is required unless gas piping is later installed.");
        } else if (gasPiping && hasActiveGasAnswer && !activeGasService) {
            nextStepTitle = "Use the no-active-gas-service documentation path.";
            nextStepSummary = "DOB requires utility-company documentation every cycle for buildings that still have gas piping but no active gas service, plus an owner statement that no appliances remain connected.";
            recommendedRoute = "/ll152-no-active-gas-service/";
            primaryCtaLabel = "Send case details";
            primaryCtaIntent = "filing_help";
            scenarioKey = "no_active_gas_service";
            nextActionChecklist = List.of(
                    "Get utility documentation showing the last date gas was supplied and the date the building was fully deactivated.",
                    "Prepare the owner statement that no gas appliances remain connected.",
                    "Plan to re-submit this documentation every cycle until service resumes or the piping condition changes."
            );
            sourceIds = List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "rcny-103-10");
            rationale.add("Before gas service resumes, the owner must obtain permits, complete required gas inspections, and submit the certification through the DOB portal.");
        } else if (gasPiping && activeGasService) {
            nextStepTitle = "Prepare the inspection, GPS1, and GPS2 filing path.";
            nextStepSummary = "For an active gas-service building, DOB says the LMP provides GPS1 within "
                    + rules.filingTimeline().gps1ToOwnerDays() + " days, and the owner must submit GPS2 within "
                    + rules.filingTimeline().gps2ToDobDays() + " days of inspection.";
            recommendedRoute = "/filing-next-step/";
            primaryCtaLabel = "Send case details";
            primaryCtaIntent = "lmp_help";
            scenarioKey = "active_gas_service";
            nextActionChecklist = List.of(
                    "Confirm BIN or BBL, community district, and who will perform the inspection before the filing window closes.",
                    "Track GPS1 to owner within " + rules.filingTimeline().gps1ToOwnerDays() + " days and GPS2 to DOB within " + rules.filingTimeline().gps2ToDobDays() + " days of inspection.",
                    "If corrections are required, calendar the follow-up certification deadline immediately."
            );
            sourceIds = List.of("periodic-gas-piping-inspections", "ll152-faqs", "nyc311-gps2-certification");
            rationale.add("If conditions need correction, the follow-up certification is generally due within "
                    + rules.filingTimeline().correctionAfterInspectionDays() + " days, or "
                    + rules.filingTimeline().correctionWithAdditionalTimeDays() + " days when additional time was requested.");
        } else {
            nextStepTitle = "Tighten the gas-status split before you rely on the filing path.";
            nextStepSummary = "The main official branch point is whether the building has no gas piping, still has gas piping but no active service, or has gas piping with active service.";
            recommendedRoute = "/filing-next-step/";
            primaryCtaLabel = "Send case details";
            primaryCtaIntent = "filing_help";
            scenarioKey = "needs_gas_split";
            nextActionChecklist = List.of(
                    "Decide whether the building has no gas piping, gas piping with no active service, or gas piping with active service.",
                    "Add the community district so the due-cycle answer can match the official sub-cycle.",
                    "Add a DOF class or reliable building profile before you rely on the filing branch."
            );
            sourceIds = List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "cycle-2-service-notice");
            rationale.add("Follow Up #7 made the no-gas-piping and no-active-gas-service paths more different, not less.");
        }

        if (buildingProfile == BuildingProfile.THREE_FAMILY) {
            rationale.add("The current rule text sets the civil penalty for a 3-family building at $" + rules.penaltySchedule().threeFamilyBuilding()
                    + ", compared with $" + rules.penaltySchedule().allOtherBuildings() + " for most other buildings.");
        }

        String confidenceLabel = buildConfidenceLabel(
                scenarioKey,
                buildingProfile,
                dofClass,
                request.communityDistrict(),
                hasGasPipingAnswer,
                gasPiping,
                hasActiveGasAnswer
        );
        String confidenceReason = buildConfidenceReason(
                scenarioKey,
                buildingProfile,
                dofClass,
                request.communityDistrict(),
                hasGasPipingAnswer,
                gasPiping,
                hasActiveGasAnswer
        );
        String reviewBoundary = buildReviewBoundary(scenarioKey);
        List<OfficialSourceLink> officialSources = sourceService.officialLinksFor(sourceIds);

        return new CheckerResult(
                coverageVerdict,
                dueCycleVerdict,
                nextStepTitle,
                nextStepSummary,
                recommendedRoute,
                primaryCtaLabel,
                primaryCtaIntent,
                scenarioKey,
                confidenceLabel,
                confidenceReason,
                reviewBoundary,
                List.copyOf(nextActionChecklist),
                officialSources,
                List.copyOf(rationale),
                "Use the official city references below as the final check before you submit anything in DOB NOW."
        );
    }

    private String buildConfidenceLabel(
            String scenarioKey,
            BuildingProfile buildingProfile,
            String dofClass,
            Integer communityDistrict,
            boolean hasGasPipingAnswer,
            boolean gasPiping,
            boolean hasActiveGasAnswer
    ) {
        if ("needs_gas_split".equals(scenarioKey)) {
            return "Low confidence. The gas-status branch is still unresolved.";
        }

        boolean hasPropertySignal = StringUtils.hasText(dofClass) || buildingProfile != BuildingProfile.UNKNOWN;
        boolean hasDueCycleSignal = communityDistrict != null;
        boolean hasGasBranchSignal = hasGasPipingAnswer && (!gasPiping || hasActiveGasAnswer);

        if (hasPropertySignal && hasDueCycleSignal && hasGasBranchSignal) {
            return "High confidence. Property profile, district, and gas-status branch are all explicit.";
        }

        if ((hasPropertySignal && hasDueCycleSignal) || (hasPropertySignal && hasGasBranchSignal) || (hasDueCycleSignal && hasGasBranchSignal)) {
            return "Medium confidence. The likely path is clear, but one core property input is still missing.";
        }

        return "Low confidence. Add district, property classification, and gas-status detail before relying on this verdict.";
    }

    private String buildConfidenceReason(
            String scenarioKey,
            BuildingProfile buildingProfile,
            String dofClass,
            Integer communityDistrict,
            boolean hasGasPipingAnswer,
            boolean gasPiping,
            boolean hasActiveGasAnswer
    ) {
        boolean hasPropertySignal = StringUtils.hasText(dofClass) || buildingProfile != BuildingProfile.UNKNOWN;
        boolean hasDueCycleSignal = communityDistrict != null;
        boolean hasGasBranchSignal = hasGasPipingAnswer && (!gasPiping || hasActiveGasAnswer);

        if ("needs_gas_split".equals(scenarioKey)) {
            return "One missing fact still changes the answer: confirm whether the building has no gas piping, no active gas service, or active gas service.";
        }

        if (!hasGasBranchSignal) {
            return "The gas-status branch is still incomplete. Confirm gas piping first, then whether service is active.";
        }

        if (!hasDueCycleSignal) {
            return "Add the community district to lock the due window against the current official cycle map.";
        }

        if (!hasPropertySignal) {
            return "Add the DOF class or a reliable building profile to tighten the coverage answer.";
        }

        return switch (scenarioKey) {
            case "possible_exempt_building" ->
                    "This answer leans exempt because the property signal already points toward DOB's exempt side of the rule.";
            case "no_gas_piping" ->
                    "This answer is stable because the building is explicitly on the no-gas-piping branch.";
            case "no_active_gas_service" ->
                    "This answer is stable because the building still has gas piping but no active gas service.";
            case "active_gas_service" ->
                    "This answer is stable because the property profile, district, and active-gas branch are all explicit.";
            default ->
                    "The core branch inputs are explicit, so the remaining work is the filing step, not the broad LL152 decision.";
        };
    }

    private String buildReviewBoundary(String scenarioKey) {
        return switch (scenarioKey) {
            case "possible_exempt_building" ->
                    "Escalate before acting if the city notice still conflicts with the property record or the exemption depends on a shaky classification.";
            case "no_gas_piping" ->
                    "Escalate before filing if any active, abandoned, or hard-to-confirm piping may still remain in the building.";
            case "no_active_gas_service" ->
                    "Escalate before filing if the utility shutoff dates, appliance removal, or owner statement details are incomplete.";
            case "active_gas_service" ->
                    "Escalate when inspection scheduling, LMP coordination, GPS1, GPS2, or correction timing is now the blocker.";
            default ->
                    "Escalate when the gas-status branch or the official property record can still change the next step you would take.";
        };
    }

    private String buildCoverageVerdict(BuildingProfile buildingProfile, String dofClass, boolean exemptDofClass, List<String> rationale, CheckerRules rules) {
        if (exemptDofClass) {
            rationale.add("DOB lists DOF class " + dofClass + " in the exempt set for LL152 coverage checks.");
            return "Likely not covered. DOB lists DOF class " + dofClass + " as an exempt classification on the current LL152 inspection page.";
        }

        return switch (buildingProfile) {
            case ONE_OR_TWO_FAMILY -> {
                rationale.add("One- and two-family homes are outside the core LL152 requirement, but misclassification can still create notices.");
                yield "Likely not covered if the DOB or DOF profile truly shows a one- or two-family exempt classification. Verify the DOF class before you rely on that answer.";
            }
            case THREE_FAMILY -> {
                rationale.add("Three-family buildings are not automatically exempt, and the current penalty schedule treats them separately from larger buildings.");
                yield "Three-family buildings are not automatically exempt. Verify the DOF class and current profile before treating the building as outside LL152.";
            }
            case MULTIFAMILY, MIXED_USE_OR_COMMERCIAL -> {
                if (StringUtils.hasText(dofClass)) {
                    rationale.add("DOF class " + dofClass + " is not in the current exempt set.");
                    yield "Likely covered. The current DOF class is not in DOB's exempt list, so treat this as an LL152 filing path until the official property record shows otherwise.";
                }

                yield "Likely covered. Treat this as an LL152 filing path until DOB records show an exemption.";
            }
            case UNKNOWN -> {
                if (StringUtils.hasText(dofClass)) {
                    rationale.add("The entered DOF class is often a stronger coverage signal than a broad building-profile guess.");
                    yield "Coverage leans toward LL152 applying because the DOF class is not in the current exempt list, but verify the property profile before you file.";
                }

                yield "Coverage still needs verification. Add a DOF class, building profile, or official property identifier for a tighter verdict.";
            }
        };
    }

    private String buildDueCycleVerdict(Integer communityDistrict, SubCycleWindow subCycleWindow, List<String> rationale, CheckerRules rules) {
        if (communityDistrict == null) {
            rationale.add("A community district is the fastest official way to place the property into the current four-year inspection cycle.");
            return "The due-cycle answer is still broad. Add a community district, BIN, or BBL before you rely on the timing.";
        }

        if (subCycleWindow == null) {
            return "The district did not match the current LL152 cycle map. Verify the community district before you rely on the timing.";
        }

        int windowYear = subCycleWindow.windowStart().getYear();
        String windowText = DATE_FORMAT.format(subCycleWindow.windowStart()) + " through " + DATE_FORMAT.format(subCycleWindow.windowEnd());

        if (windowYear == rules.activeCycleYear()) {
            rationale.add("DOB places Community District " + communityDistrict + " in Cycle 2, Sub-cycle " + subCycleWindow.subCycle() + " for " + rules.activeCycleYear() + ".");
            rationale.add("If the inspection cannot be completed by the due date, DOB allows one " + rules.filingTimeline().inspectionExtensionDays() + "-day extension per cycle.");
            return "Community District " + communityDistrict + " is in Cycle 2, Sub-cycle " + subCycleWindow.subCycle()
                    + ". The official inspection window is " + windowText + ".";
        }

        if (windowYear < rules.activeCycleYear()) {
            rationale.add("This district was due earlier in Cycle 2, so a missed filing now usually means the issue is correction, penalty, or waiver rather than the original due date.");
            return "Community District " + communityDistrict + " was due in Cycle 2, Sub-cycle " + subCycleWindow.subCycle()
                    + " during " + windowText + ". If nothing was filed, focus on the correction, violation, or waiver path now.";
        }

        rationale.add("This district is not in the current active 2026 sub-cycle.");
        return "Community District " + communityDistrict + " is scheduled for Cycle 2, Sub-cycle " + subCycleWindow.subCycle()
                + " during " + windowText + ". In 2026, this is an advance-planning question rather than the active due year.";
    }

    private SubCycleWindow findSubCycleWindow(CheckerRules rules, Integer communityDistrict) {
        return rules.cycle2Schedule().stream()
                .filter(window -> window.containsDistrict(communityDistrict))
                .findFirst()
                .orElse(null);
    }

    private String normalizeDofClass(String dofClass) {
        if (!StringUtils.hasText(dofClass)) {
            return null;
        }

        return dofClass.trim().toUpperCase(Locale.ROOT);
    }
}
