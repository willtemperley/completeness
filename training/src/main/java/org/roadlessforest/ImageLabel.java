package org.roadlessforest;

public class ImageLabel {

  private Long id;

  private Long label;

  private String hash;

  private boolean training;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getLabel() {
    return label;
  }

  public void setLabel(Long label) {
    this.label = label;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  public boolean isTraining() {
    return training;
  }

  public void setTraining(boolean training) {
    this.training = training;
  }
}
