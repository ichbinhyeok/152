@import java.util.List
@import owner.nycll152.pages.HomeBlockerCard
@import owner.nycll152.pages.RoutePage
@param String currentPath
@param String siteUrl
@param String canonicalUrl
@param String teamSourceCheckedAt
@param int teamSourceCount
@param boolean showTeamTrust
@param String organizationStructuredData
@param String websiteStructuredData
@param List<HomeBlockerCard> blockerCards
@param List<RoutePage> coreRoutes
@param List<RoutePage> trustRoutes
@param List<RoutePage> heldRoutes

@template.layout.page(
    currentPath = currentPath,
    pageTitle = "NYC Local Law 152 checker: does this building need inspection?",
    metaDescription = "Use the NYC Local Law 152 checker to see whether a building likely needs inspection, which cycle applies, and whether the next step is no gas piping, no active gas service, GPS1, or GPS2.",
    canonicalUrl = canonicalUrl,
    noindex = false,
    teamSourceCheckedAt = teamSourceCheckedAt,
    teamSourceCount = teamSourceCount,
    showTeamTrust = showTeamTrust,
    content = @`
        <script type="application/ld+json">
            $unsafe{organizationStructuredData}
        </script>
        <script type="application/ld+json">
            $unsafe{websiteStructuredData}
        </script>
        <div class="home-page">
            <section class="home-hero">
                <div class="home-hero__inner">
                    <div class="home-hero__copy">
                        <p class="eyebrow">NYC Local Law 152</p>
                        <h1>Know the filing path before DOB turns into guesswork.</h1>
                        <p class="home-hero__summary">Start with the building facts you already know. The checker narrows coverage, due timing, and the gas-status split before you spend time in DOB NOW.</p>
                        <div class="home-hero__actions">
                            <a class="button" href="/ll152-checker/">Run checker</a>
                            <button class="ghost-link ghost-link--light" type="button" data-lead-open data-route-id="home-hero" data-route-path="/" data-intent="filing_help">Send case details</button>
                        </div>
                        <div class="home-hero__notes">
                            <div class="home-hero__note">
                                <span>Coverage and timing</span>
                                <p>See whether the building is likely covered and whether the live problem is this cycle or a missed one.</p>
                            </div>
                            <div class="home-hero__note">
                                <span>Gas-status split</span>
                                <p>Separate no gas piping, no active gas service, and active service before you move to the next filing step.</p>
                            </div>
                            <div class="home-hero__note">
                                <span>Source check</span>
                                <p>Checked ${teamSourceCheckedAt} against ${teamSourceCount} official city references.</p>
                            </div>
                        </div>
                    </div>
                    <div class="home-hero__rail">
                        <p class="home-hero__rail-label">Built for owners, supers, and managers</p>
                        <h2>Use this when you need the practical answer, not a generic LL152 explainer.</h2>
                        <ul class="home-hero__rail-list">
                            <li>Covered or exempt?</li>
                            <li>Is the district timing still live or already behind the building?</li>
                            <li>Is this no gas piping or no active gas service?</li>
                        </ul>
                        <p class="home-hero__contact">Virtual team contact: shinhyeok22@gmail.com</p>
                    </div>
                </div>
            </section>

            <section class="home-blockers" id="pick-the-blocker">
                <div class="home-section-heading">
                    <p class="eyebrow">Start with the question</p>
                    <h2>Open the question that is actually slowing the filing.</h2>
                    <p>The right page depends on the real question, not on how much of the statute you can remember from last cycle.</p>
                </div>
                <div class="home-blocker-list">
                    @for(int i = 0; i < blockerCards.size(); i++)
                        <a class="home-blocker" href="${blockerCards.get(i).path()}">
                            <span class="home-blocker__index">${String.format("%02d", i + 1)}</span>
                            <div class="home-blocker__content">
                                <p class="eyebrow">${blockerCards.get(i).eyebrow()}</p>
                                <h3>${blockerCards.get(i).title()}</h3>
                                <p>${blockerCards.get(i).summary()}</p>
                            </div>
                        </a>
                    @endfor
                </div>
            </section>

            @if(!coreRoutes.isEmpty())
                <section class="home-routes">
                    <div class="home-section-heading">
                        <p class="eyebrow">Core guides</p>
                        <h2>Open the page that matches the real filing question.</h2>
                        <p>Keep the checker for uncertainty. When the question is clear, go straight to the page that answers it.</p>
                    </div>
                    <div class="home-routes__layout">
                        <article class="home-route-feature">
                            <a class="home-route-feature__media" href="${coreRoutes.get(0).path()}">
                                <img src="${coreRoutes.get(0).imageUrl()}" alt="${coreRoutes.get(0).imageAlt()}">
                            </a>
                            <div class="home-route-feature__body">
                                <p class="eyebrow">${coreRoutes.get(0).eyebrow()}</p>
                                <h3>${coreRoutes.get(0).title()}</h3>
                                <p>${coreRoutes.get(0).summary()}</p>
                                <div class="home-route-feature__meta">
                                    <span>Updated for current rules</span>
                                    <span>Checked ${teamSourceCheckedAt}</span>
                                </div>
                                <div class="home-route-feature__actions">
                                    <a class="button" href="${coreRoutes.get(0).path()}">Open guide</a>
                                    <button class="ghost-button" type="button" data-lead-open data-route-id="${coreRoutes.get(0).id()}" data-route-path="${coreRoutes.get(0).path()}" data-intent="${coreRoutes.get(0).ctaIntent()}">${coreRoutes.get(0).ctaLabel()}</button>
                                </div>
                            </div>
                        </article>

                        @if(coreRoutes.size() > 1)
                            <div class="home-route-list">
                                @for(int i = 1; i < coreRoutes.size(); i++)
                                    <article class="home-route-list__item">
                                        <a class="home-route-list__media" href="${coreRoutes.get(i).path()}">
                                            <img src="${coreRoutes.get(i).imageUrl()}" alt="${coreRoutes.get(i).imageAlt()}">
                                        </a>
                                        <div class="home-route-list__body">
                                            <p class="eyebrow">${coreRoutes.get(i).eyebrow()}</p>
                                            <h3><a href="${coreRoutes.get(i).path()}">${coreRoutes.get(i).title()}</a></h3>
                                            <p>${coreRoutes.get(i).summary()}</p>
                                        </div>
                                        <div class="home-route-list__actions">
                                            <a class="text-link" href="${coreRoutes.get(i).path()}">Open guide</a>
                                            <button class="ghost-button" type="button" data-lead-open data-route-id="${coreRoutes.get(i).id()}" data-route-path="${coreRoutes.get(i).path()}" data-intent="${coreRoutes.get(i).ctaIntent()}">${coreRoutes.get(i).ctaLabel()}</button>
                                        </div>
                                    </article>
                                @endfor
                            </div>
                        @endif
                    </div>
                </section>
            @endif

            <section class="home-guidance">
                <article class="home-guidance__panel">
                    <p class="eyebrow">Self-serve when</p>
                    <h2>The branch is already clear.</h2>
                    <p>Keep moving yourself when the remaining work is preparation, timing, or document sequence rather than deciding which page applies.</p>
                    <ul class="home-guidance__list">
                        <li>The building looks clearly covered or clearly exempt.</li>
                        <li>The district timing is explicit or obviously not the live blocker.</li>
                        <li>The gas-status branch is already settled.</li>
                        <li>The remaining question is what to prepare next, not which page is correct.</li>
                    </ul>
                </article>

                <article class="home-guidance__panel home-guidance__panel--accent">
                    <p class="eyebrow">Contact now when</p>
                    <h2>One missing fact still changes the answer.</h2>
                    <p>Send case details before the wrong filing step creates cleanup and another round of DOB guesswork.</p>
                    <ul class="home-guidance__list">
                        <li>The next filing step is still unclear even though coverage looks likely.</li>
                        <li>No gas piping versus no active gas service is still unresolved.</li>
                        <li>DOB or DOF records still conflict with what you believe the building is.</li>
                        <li>Inspection timing, GPS1, GPS2, or corrections now control the next move.</li>
                    </ul>
                    <button class="button home-guidance__cta" type="button" data-lead-open data-route-id="home-guidance" data-route-path="/" data-intent="filing_help">Send case details</button>
                </article>
            </section>

            @if(!trustRoutes.isEmpty())
                <section class="home-trust-surface">
                    <div class="home-section-heading">
                        <p class="eyebrow">Trust and boundaries</p>
                        <h2>Read the boundaries before you share a building.</h2>
                        <p>Privacy, disclosure, contact rules, and methodology live here so the filing question stays separate from everything else.</p>
                    </div>
                    <div class="home-trust-links">
                        @for(var route : trustRoutes)
                            <a class="home-trust-link" href="${route.path()}">
                                <span class="home-trust-link__title">${route.title()}</span>
                                <span class="home-trust-link__summary">${route.summary()}</span>
                            </a>
                        @endfor
                    </div>
                </section>
            @endif
        </div>
    `
)
