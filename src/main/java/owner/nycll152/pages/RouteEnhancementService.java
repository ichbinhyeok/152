package owner.nycll152.pages;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteEnhancementService {

    public List<HomeBlockerCard> homeBlockers() {
        return List.of(
                new HomeBlockerCard(
                        "Due now",
                        "Does this building need LL152 inspection?",
                        "Start here when you still need the likely coverage, current cycle timing, or the gas-status split.",
                        "/ll152-checker/"
                ),
                new HomeBlockerCard(
                        "Next filing step",
                        "What is the next LL152 filing step?",
                        "Use this when the building already looks covered and the main question is what gets filed next.",
                        "/filing-next-step/"
                ),
                new HomeBlockerCard(
                        "Gas-status split",
                        "Is this no gas piping or no active gas service?",
                        "Use the comparison page that prevents the most common LL152 filing mistake before you open DOB NOW.",
                        "/no-gas-vs-no-active-gas-service/"
                ),
                new HomeBlockerCard(
                        "Penalty or waiver",
                        "Can this LL152 penalty be fixed, extended, or waived?",
                        "Use this when the original due window may already be behind the building and the open issue is penalty, challenge, or waiver.",
                        "/extension-penalty-waiver/"
                ),
                new HomeBlockerCard(
                        "After inspection",
                        "What happens after inspection, GPS1, or GPS2?",
                        "Use this timing page after the inspection branch is already clear and the next issue is submission timing.",
                        "/after-inspection-gps1-gps2/"
                )
        );
    }

    public RouteEnhancement forPage(RoutePage page) {
        if ("district-overlay".equals(page.family())) {
            return new RouteEnhancement(
                    List.of(
                            "Confirm the community district against the current Cycle 2 map before treating the timing as final.",
                            "Use the checker to lock the gas-status branch before you treat a district note as the filing answer.",
                            "Treat district overlays as support context, not as the main answer."
                    ),
                    List.of(
                            "The district looks right, but the gas-status branch is still unresolved.",
                            "The district timing conflicts with the notice or what DOB NOW is showing.",
                            "A timing question is masking a deeper filing or correction-path question."
                    ),
                    List.of("periodic-gas-piping-inspections", "cycle-2-service-notice")
            );
        }

        return switch (page.slug()) {
            case "filing-next-step" -> new RouteEnhancement(
                    List.of(
                            "Confirm whether the building has no gas piping, no active gas service, or active gas service before opening the filing path.",
                            "Collect BIN or BBL, the community district, and the owner contact that will receive GPS1 or certification follow-up.",
                            "Escalate to LMP or filing help only after the branch is explicit."
                    ),
                    List.of(
                            "The building may actually belong on the no-gas-piping or no-active-gas-service page.",
                            "You still do not know who must act next: owner, utility, or LMP.",
                            "The page seems directionally right, but the next filing step still changes when new facts appear."
                    ),
                    List.of("periodic-gas-piping-inspections", "ll152-faqs", "nyc311-gps2-certification")
            );
            case "no-gas-vs-no-active-gas-service" -> new RouteEnhancement(
                    List.of(
                        "Use the no-gas-piping path only when the building truly has no gas piping anywhere in the system.",
                        "Use the no-active-gas-service path only when piping still exists but utility records show the building was fully deactivated.",
                        "Keep the utility statement, owner statement, and signer details ready before you submit anything through DOB NOW."
                    ),
                    List.of(
                            "You cannot prove whether piping still exists somewhere in the building.",
                            "Utility records, shutoff dates, or appliance-removal facts are incomplete.",
                            "The answer looks close, but you are still guessing between one-time certification and recurring-cycle documentation."
                    ),
                    List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "rcny-103-10")
            );
            case "ll152-no-gas-piping-certification" -> new RouteEnhancement(
                    List.of(
                            "Confirm there is no gas piping anywhere in the building before using this one-time certification path.",
                            "Collect BIN or BBL plus the signer details for the utility company, registered design professional, or LMP who will certify the condition.",
                            "Keep this page separate from no-active-gas-service cases, which stay recurring and cycle-sensitive."
                    ),
                    List.of(
                            "You are relying on a utility shutoff alone, but piping may still exist in the building.",
                            "Abandoned, hidden, or hard-to-confirm piping has not been ruled out.",
                            "The building may still belong on the no-active-gas-service path instead of the one-time no-piping certification path."
                    ),
                    List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "rcny-103-10")
            );
            case "ll152-no-active-gas-service" -> new RouteEnhancement(
                    List.of(
                            "Confirm gas piping still exists before using the no-active-gas-service route.",
                            "Collect utility documentation showing when the building stopped receiving gas service and the date the service was fully deactivated.",
                            "Keep the owner statement ready and plan for recurring-cycle documentation until the piping condition changes."
                    ),
                    List.of(
                            "You cannot prove whether gas piping still exists in the building.",
                            "Utility shutoff dates, owner statements, or appliance-removal facts are incomplete.",
                            "The case may still need inspection, correction, or another filing page beyond the recurring no-active-service documentation."
                    ),
                    List.of("periodic-gas-piping-inspections", "ll152-follow-up-7", "rcny-103-10")
            );
            case "extension-penalty-waiver" -> new RouteEnhancement(
                    List.of(
                            "Match the building type and notice date before assuming the civil-penalty amount.",
                            "Prepare supporting documents for any challenge or waiver request within the notice window.",
                            "Treat violation resolution as both a payment and filing problem, not only a payment problem."
                    ),
                    List.of(
                            "You still have not identified the missed filing or correction step behind the penalty notice.",
                            "The notice date, building type, or waiver basis is still uncertain.",
                            "The penalty question is hiding an unresolved gas-status or next-step problem."
                    ),
                    List.of("cycle-2-service-notice", "ll152-follow-up-7", "rcny-103-10")
            );
            case "after-inspection-gps1-gps2" -> new RouteEnhancement(
                    List.of(
                            "Track the inspection date first because GPS1 and GPS2 timing flows from that date.",
                            "Confirm who will submit GPS2 in DOB NOW and who will receive GPS1 from the LMP.",
                            "If correction work is required, calendar the follow-up certification deadline immediately."
                    ),
                    List.of(
                            "The inspection outcome still changes whether the next move is GPS2, correction, or another filing.",
                            "Nobody clearly owns GPS1 receipt, GPS2 submission, or correction follow-up.",
                            "The timing slipped far enough that the problem may now be penalty, waiver, or late correction handling."
                    ),
                    List.of("periodic-gas-piping-inspections", "ll152-faqs", "rcny-103-10")
            );
            case "2026-deadline" -> new RouteEnhancement(
                    List.of(
                            "Verify the current community district before treating the 2026 window as final.",
                            "If the district was due earlier, shift from deadline thinking to correction, challenge, or waiver thinking.",
                            "Keep the due-year answer attached to the actual branch the building is on."
                    ),
                    List.of(
                            "The district might be right, but you still do not know which filing branch the building belongs to.",
                            "The timing answer conflicts with what the notice, BIN, or BBL lookup suggests.",
                            "The original due window may already be behind the building, which changes the question from due date to remediation."
                    ),
                    List.of("periodic-gas-piping-inspections", "cycle-2-service-notice")
            );
            case "exempt-building-notification" -> new RouteEnhancement(
                    List.of(
                            "Verify the official DOF class, BIN, or BBL before relying on an exemption answer.",
                            "Keep the property-record evidence ready if the city notice still treats the building as covered.",
                            "Use the exempt-building route before you pay or file against the wrong classification."
                    ),
                    List.of(
                            "The property record still conflicts with the notice or violation path.",
                            "You are relying on a broad building label, but the DOF class or official profile is not settled.",
                            "The exemption answer matters enough that a wrong assumption would trigger the wrong filing or payment response."
                    ),
                    List.of("periodic-gas-piping-inspections", "cycle-2-service-notice")
            );
            default -> new RouteEnhancement(List.of(), List.of(), List.of());
        };
    }
}
