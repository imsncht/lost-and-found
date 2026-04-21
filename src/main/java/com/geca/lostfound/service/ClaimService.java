package com.geca.lostfound.service;

import com.geca.lostfound.dao.ClaimDAO;
import com.geca.lostfound.dao.ItemDAO;
import com.geca.lostfound.model.Claim;
import com.geca.lostfound.model.Item;
import com.geca.lostfound.model.User;

import java.util.List;

public class ClaimService {

    private ClaimDAO claimDAO = new ClaimDAO();
    private ItemDAO itemDAO = new ItemDAO();

    public void submitClaim(Long itemId, User claimant, String message) {

        Item item = itemDAO.findById(itemId);

        if (item == null) return;

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setMessage(message);
        claim.setStatus("PENDING");

        claimDAO.save(claim);
    }

    public List<Claim> getPendingClaims() {
        return claimDAO.getAllPendingClaims();
    }

    public void approve(Long claimId) {

        Claim claim = claimDAO.findById(claimId);

        if (claim != null) {
            claim.setStatus("APPROVED");
            claimDAO.update(claim);

            Item item = claim.getItem();
            item.setStatus("CLOSED");
            itemDAO.update(item);
        }
    }

    public void reject(Long claimId) {

        Claim claim = claimDAO.findById(claimId);

        if (claim != null) {
            claim.setStatus("REJECTED");
            claimDAO.update(claim);
        }
    }
}