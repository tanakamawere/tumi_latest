package org.tmz.tumi.Objects;

public class AdvertisementsObject {
    String adProductTitle,
            adProductPrice,
            adProductDescription,
            adKey,
            adBusinessName,
            adBusinessPhone,
            adBusinessAddress,
            imageURL,
            adVertiserKey,
            adTags,
            adCategory;

    public AdvertisementsObject(String adProductTitle, String adProductPrice, String adProductDescription, String adKey, String adBusinessName, String adBusinessPhone, String adBusinessAddress, String imageURL, String adVertiserKey, String adTags, String adCategory) {
        this.adProductTitle = adProductTitle;
        this.adProductPrice = adProductPrice;
        this.adProductDescription = adProductDescription;
        this.adKey = adKey;
        this.adBusinessName = adBusinessName;
        this.adBusinessPhone = adBusinessPhone;
        this.adBusinessAddress = adBusinessAddress;
        this.imageURL = imageURL;
        this.adVertiserKey = adVertiserKey;
        this.adTags = adTags;
        this.adCategory = adCategory;
    }

    public String getAdProductTitle() {
        return adProductTitle;
    }

    public void setAdProductTitle(String adProductTitle) {
        this.adProductTitle = adProductTitle;
    }

    public String getAdProductPrice() {
        return adProductPrice;
    }

    public void setAdProductPrice(String adProductPrice) {
        this.adProductPrice = adProductPrice;
    }

    public String getAdProductDescription() {
        return adProductDescription;
    }

    public void setAdProductDescription(String adProductDescription) {
        this.adProductDescription = adProductDescription;
    }

    public String getAdKey() {
        return adKey;
    }

    public void setAdKey(String adKey) {
        this.adKey = adKey;
    }

    public String getAdBusinessName() {
        return adBusinessName;
    }

    public void setAdBusinessName(String adBusinessName) {
        this.adBusinessName = adBusinessName;
    }

    public String getAdBusinessPhone() {
        return adBusinessPhone;
    }

    public void setAdBusinessPhone(String adBusinessPhone) {
        this.adBusinessPhone = adBusinessPhone;
    }

    public String getAdBusinessAddress() {
        return adBusinessAddress;
    }

    public void setAdBusinessAddress(String adBusinessAddress) {
        this.adBusinessAddress = adBusinessAddress;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAdVertiserKey() {
        return adVertiserKey;
    }

    public void setAdVertiserKey(String adVertiserKey) {
        this.adVertiserKey = adVertiserKey;
    }

    public String getAdTags() {
        return adTags;
    }

    public void setAdTags(String adTags) {
        this.adTags = adTags;
    }

    public String getAdCategory() {
        return adCategory;
    }

    public void setAdCategory(String adCategory) {
        this.adCategory = adCategory;
    }
}
