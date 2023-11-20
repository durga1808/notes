package com.zaga.entity.queryentity.metrics;

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
@MongoEntity(collection = "KeplerMetricDTO", database = "OtelMetric")
public class KeplerMetricDTO {
    private Date date;
    private Double powerConsumption;
    private Integer memoryUsage;
    private String serviceName;
    private String type ;
    private long observedTimeMillis ;


    

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
     * @return Integer return the memoryUsage
     */
    public Integer getMemoryUsage() {
        return memoryUsage;
    }

    /**
     * @param memoryUsage the memoryUsage to set
     */
    public void setMemoryUsage(Integer memoryUsage) {
        this.memoryUsage = memoryUsage;
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
     * @return String return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return long return the observedTimeMillis
     */
    public long getObservedTimeMillis() {
        return observedTimeMillis;
    }

    /**
     * @param observedTimeMillis the observedTimeMillis to set
     */
    public void setObservedTimeMillis(long observedTimeMillis) {
        this.observedTimeMillis = observedTimeMillis;
    }

    @Override
    public String toString() {
        return "KeplerMetricDTO [date=" + date + ", powerConsumption=" + powerConsumption + ", memoryUsage="
                + memoryUsage + ", serviceName=" + serviceName + ", type=" + type + ", observedTimeMillis="
                + observedTimeMillis + "]";
    }

}
