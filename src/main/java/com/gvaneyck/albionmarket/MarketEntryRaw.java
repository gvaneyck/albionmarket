package com.gvaneyck.albionmarket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketEntryRaw {
    @JsonProperty("Id")
    private long id;
    
    @JsonProperty("UnitPriceSilver")
    private long price;

    @JsonProperty("TotalPriceSilver")
    private long totalPrice;
    
    @JsonProperty("Amount")
    private long qty;
    
    @JsonProperty("Tier")
    private long tier;
    
    @JsonProperty("IsFinished")
    private boolean finished;
    
    @JsonProperty("AuctionType")
    private String auctionType;
    
    @JsonProperty("HasBuyerFetched")
    private boolean buyerFetched;
    
    @JsonProperty("HasSellerFetched")
    private boolean sellerFetched;
    
    @JsonProperty("SellerCharacterId")
    private String sellerId;
    
    @JsonProperty("SellerName")
    private String sellerName;
    
    @JsonProperty("BuyerCharacterId")
    private String buyerId;
    
    @JsonProperty("BuyerName")
    private String buyerName;
    
    @JsonProperty("ItemTypeId")
    private String type;
    
    @JsonProperty("ItemGroupTypeId")
    private String groupType;
    
    @JsonProperty("EnchantmentLevel")
    private long enchantment;
    
    @JsonProperty("QualityLevel")
    private long quality;
    
    @JsonProperty("Expires")
    private String expires;

    public long getPrice() {
        return price / 10000;
    }

    public long getTotalPrice() {
        return totalPrice / 10000;
    }

    public String getFullType() {
        return type + "_" + quality;
    }
}
