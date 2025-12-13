package com.acharyaamrit.medicare.doctor.model;

public class Medicine {
    int id;
    String name;
    String generic_name;
    String company_name;
    String category_name;
    String code;
    String manu_date;
    String exp_date;
    String doasage_form;

    String doasage_unit;
    String description;
    String side_effects;
    String image;

    public Medicine() {
    }

    public Medicine(int id, String name, String generic_name, String company_name, String category_name, String code, String manu_date, String exp_date, String doasage_form, String doasage_unit, String description, String side_effects, String image) {
        this.id = id;
        this.name = name;
        this.generic_name = generic_name;
        this.company_name = company_name;
        this.category_name = category_name;
        this.code = code;
        this.manu_date = manu_date;
        this.exp_date = exp_date;
        this.doasage_form = doasage_form;
        this.doasage_unit = doasage_unit;
        this.description = description;
        this.side_effects = side_effects;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeneric_name() {
        return generic_name;
    }

    public void setGeneric_name(String generic_name) {
        this.generic_name = generic_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getManu_date() {
        return manu_date;
    }

    public void setManu_date(String manu_date) {
        this.manu_date = manu_date;
    }

    public String getExp_date() {
        return exp_date;
    }

    public void setExp_date(String exp_date) {
        this.exp_date = exp_date;
    }

    public String getDoasage_form() {
        return doasage_form;
    }

    public void setDoasage_form(String doasage_form) {
        this.doasage_form = doasage_form;
    }

    public String getDoasage_unit() {
        return doasage_unit;
    }

    public void setDoasage_unit(String doasage_unit) {
        this.doasage_unit = doasage_unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSide_effects() {
        return side_effects;
    }

    public void setSide_effects(String side_effects) {
        this.side_effects = side_effects;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
