package com.geca.lostfound.model;

import javax.persistence.*;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "itemId")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "claimantId")
    private User claimant;

    @Column(length = 3000)
    private String message;

    private String status = "PENDING";

    public Claim() {
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getClaimant() {
        return claimant;
    }

    public void setClaimant(User claimant) {
        this.claimant = claimant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    private String colorAnswer;

    @Column(length = 3000)
    private String identifyingMarks;

    @Column(length = 3000)
    private String contentsAnswer;

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public String getColorAnswer() {
        return colorAnswer;
    }

    public void setColorAnswer(String colorAnswer) {
        this.colorAnswer = colorAnswer;
    }

    public String getIdentifyingMarks() {
        return identifyingMarks;
    }

    public void setIdentifyingMarks(String identifyingMarks) {
        this.identifyingMarks = identifyingMarks;
    }

    public String getContentsAnswer() {
    return contentsAnswer;
    }

    public void setContentsAnswer(String contentsAnswer) {
        this.contentsAnswer = contentsAnswer;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
}