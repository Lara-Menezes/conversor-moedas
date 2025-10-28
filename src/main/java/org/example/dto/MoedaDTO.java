package org.example.dto;

public class MoedaDTO {
    private String from;
    private String to;
    private double amount;
    private double convertedAmount;
    private double rate;

    // Getters e Setters

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(double convertedAmount) { this.convertedAmount = convertedAmount; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}
