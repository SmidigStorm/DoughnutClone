package com.odde.doughnut.testability;

import com.odde.doughnut.models.Randomizer;
import com.odde.doughnut.models.randomizers.NonRandomizer;
import com.odde.doughnut.models.randomizers.RealRandomizer;
import com.odde.doughnut.services.GithubService;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class TestabilitySettings {
  private Timestamp timestamp = null;
  private NonRandomizer nonRandomizer = null;
  @Getter @Setter Boolean useRealGithub = true;
  @Autowired GithubService githubService;
  @Getter private boolean featureToggleEnabled = false;

  private WikidataService wikidataService;
  @Getter @Setter private Boolean useDummyWikidata = false;
  @Getter @Setter private Integer imposterApiServicePort;

  public void timeTravelTo(Timestamp timestamp) {
    this.timestamp = timestamp;
    if (nonRandomizer == null) {
      nonRandomizer = new NonRandomizer();
    }
  }

  public Timestamp getCurrentUTCTimestamp() {
    if (timestamp == null) {
      return new Timestamp(System.currentTimeMillis());
    }
    return timestamp;
  }

  public Randomizer getRandomizer() {
    if (nonRandomizer == null) {
      return new RealRandomizer();
    }
    return nonRandomizer;
  }

  public void setAlwaysChoose(String option) {
    if (nonRandomizer == null) {
      nonRandomizer = new NonRandomizer();
    }
    nonRandomizer.setAlwaysChoose(option);
  }

  public GithubService getGithubService() {
    if (useRealGithub) {
      return githubService;
    }
    return new NullGithubService();
  }

  public void enableFeatureToggle(boolean enabled) {
    this.featureToggleEnabled = enabled;
  }

  public String getWikidataUrl() {
    if (useDummyWikidata) {
      wikidataService = new WikidataService(imposterApiServicePort);
      return wikidataService.getApiUrl();
    }
    wikidataService = new WikidataService();
    return wikidataService.getApiUrl();
  }
}
