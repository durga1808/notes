package com.zaga.entity.queryentity.kepler;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("id")
@MongoEntity(collection = "KeplerMetricDTO", database = "KeplerMetric")
public class KeplerMetricDTO {
    private Date date;
    private Double powerConsumption;
    // private Integer memoryUsage;
    private String serviceName;
    // container / node/host
    private String type;
    // private String type;
    // private long observedTimeMillis ;
    private Resource resource;

    /**
     * @return Date return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return Double return the powerConsumption
     */
    public Double getPowerConsumption() {
        return powerConsumption;
    }

    /**
     * @param powerConsumption the powerConsumption to set
     */
    public void setPowerConsumption(Double powerConsumption) {
        this.powerConsumption = powerConsumption;
    }

    /**
     * @return String return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return Resource return the resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "KeplerMetricDTO [date=" + date + ", powerConsumption=" + powerConsumption + ", serviceName="
                + serviceName + ", resource=" + resource + "]";
    }

}
