package com.zaga.handler;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricHistogram;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPointAttributeValue;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPointAttributeValue;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPointAttributeValue;
import com.zaga.entity.queryentity.metrics.KeplerMetricDTO;
import com.zaga.entity.queryentity.metrics.MetricDTO;
import com.zaga.repo.KeplerMetricDTORepo;
import com.zaga.repo.KeplerMetricRepo;
import com.zaga.repo.MetricCommandRepo;
import com.zaga.repo.MetricDTORepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KeplerMetricCommandHandler {
    
    @Inject
    KeplerMetricRepo keplerMetricRepo;

    @Inject
    KeplerMetricDTORepo KeplerMetricDTORepo;


    public void createKeplerMetric(KeplerMetric metric) {

        keplerMetricRepo.persist(metric);

        List<KeplerMetricDTO> metricDTOs = extractAndMapData(metric);
        
         if (!metricDTOs.isEmpty()) {

            for ( KeplerMetricDTO keplerMetDTO : metricDTOs) {

              KeplerMetricDTORepo.persist(keplerMetDTO);

            }

          }

     }

     public List<KeplerMetricDTO> extractAndMapData(KeplerMetric keplerMetric){
     
            List<KeplerMetricDTO> keplerMetricDTOLst = new ArrayList<>();


            List<ResourceMetric> resourceMetrics = keplerMetric.getResourceMetrics();

           for (ResourceMetric resourceMetric : resourceMetrics) {

              List<ScopeMetric> scopeMetrics = resourceMetric.getScopeMetrics();

              ScopeMetric scopeMetric = scopeMetrics.get(0);

              List<Metric>  metrics = scopeMetric.getMetrics();

              KeplerMetricDTO keplerMetricDTO = null;

            

              for ( Metric metric : metrics) {
                  
                MetricGauge  metricGauge = metric.getGauge();

                String key = null;
                String value = null;
                double usage = 0;
                String type = null;
                Date createdTime = null;
                long observedTimeMillis = 0;
                String metricName = metric.getName();

                if ( metricGauge != null) {


                  List<GaugeDataPoint>  gaugeDataPointLst = metricGauge.getDataPoints();

                  for (GaugeDataPoint gaugeDataPoint : gaugeDataPointLst ){

                   StringBuffer keys = null; 


                    String dobulevle = gaugeDataPoint.getAsDouble();

                    type = new String("Gauge");
                   

                    if ( dobulevle != null ){
                    
                        usage = Double.parseDouble(dobulevle);

                    }

                    String startTimeStm = gaugeDataPoint.getStartTimeUnixNano();
                    if (startTimeStm != null ){
                      createdTime = convertUnixNanoToLocalDateTime(startTimeStm);
                    }


                    String time = gaugeDataPoint.getTimeUnixNano();

                    observedTimeMillis = Long.parseLong(time) / 1_000_000;


                    List<GaugeDataPointAttribute>  gaugeDataPointAttributes = gaugeDataPoint.getAttributes();

                      for ( GaugeDataPointAttribute gAtt : gaugeDataPointAttributes ){

                        if (keys == null) {
                          keys = new StringBuffer();
                        }

                          String keyValue = gAtt.getKey();
                          String attvalue = null;
                          //key = gAtt.getKey();

                          GaugeDataPointAttributeValue gAttValue = gAtt.getValue();

                          if (gAttValue!=null)
                          {
                           // GaugeDataPointAttributeValue gaugeDataPointAttributeValue = gAttValue.getValue();

                            attvalue = gAttValue.getStringValue();

                          }else 
                          {
                            attvalue = null;
                          }
                          if (keyValue!=null && attvalue != null ) {
                              keys.append("( ");
                              keys.append(keyValue); 
                              keys.append(":");
                              keys.append(attvalue);
                              keys.append(" )");

                          }
                        }

                          //
                    keplerMetricDTO = new KeplerMetricDTO();

                    keplerMetricDTO.setDate(createdTime);
                    keplerMetricDTO.setPowerConsumption(usage);
                    keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
                    keplerMetricDTO.setServiceName(keys != null && !keys.equals("")?  keys.toString() : metricName);
                    keplerMetricDTO.setType(type);
                    keplerMetricDTOLst.add(keplerMetricDTO);

                    }
                   

                }
                //Metric sum
                MetricSum metricSum = metric.getSum();
                if (metricSum != null) {

                    List<SumDataPoint> sumDataPoints =  metricSum.getDataPoints();


                    for (SumDataPoint sumDataPoint : sumDataPoints){
                       
                      StringBuffer keys = null;

                      String sumType = new String("Sum");
                      String startTime = sumDataPoint.getStartTimeUnixNano();

                      createdTime = convertUnixNanoToLocalDateTime(startTime);
                      
                     
                      String time = sumDataPoint.getTimeUnixNano();

                      observedTimeMillis = Long.parseLong(time) / 1_000_000;

                      List<SumDataPointAttribute> sumAtt =  sumDataPoint.getAttributes();  

                      for ( SumDataPointAttribute sumDataPointAttribute : sumAtt ){

                         if (keys == null) {
                          keys = new StringBuffer();
                        }

                          String keyValue = sumDataPointAttribute.getKey();
                         // keys.append(keyValue);
                          String attvalue = null;
                          SumDataPointAttributeValue gAttValue = sumDataPointAttribute.getValue();

                          if (gAttValue != null)
                          {

                             System.out.println("not null value " + keys.toString());
                             attvalue = gAttValue.getStringValue();
                             System.out.println("not null value " + attvalue);

                          }else 
                          {
                            System.out.println("null value " + keys.toString());
                             attvalue = null;
                          }

                          if ( keyValue != null &&  attvalue  != null ) {

                              keys.append("( ");
                              keys.append(keyValue); 
                              keys.append(":");
                              keys.append(attvalue);
                              keys.append(" )");
                            
                          }
                         
                      }
                      //create dto
                       //
                      keplerMetricDTO = new KeplerMetricDTO();

                      keplerMetricDTO.setDate(createdTime);
                      keplerMetricDTO.setPowerConsumption(usage);
                      keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
                      keplerMetricDTO.setServiceName(keys != null  ?  keys.toString() : metricName);
                      keplerMetricDTO.setType(sumType);
                      keplerMetricDTOLst.add(keplerMetricDTO);


                    }
                    
                }
                
                MetricHistogram histogram = metric.getHistogram();

                if (histogram != null) {

                   List< HistogramDataPoint > histogramDataPoints =  histogram.getDataPoints();

                    for ( HistogramDataPoint histogramDataPoint : histogramDataPoints){
                      
                      StringBuffer keys = null;

                      
                      type = new String("Histogram");

                      String startTime = histogramDataPoint.getStartTimeUnixNano();

                      createdTime = convertUnixNanoToLocalDateTime(startTime);
                      
                      //System.out.println("startTime==>" + startTime );
                     
                      String time = histogramDataPoint.getTimeUnixNano();

                      observedTimeMillis = Long.parseLong(time) / 1_000_000;

                        
                      //System.out.println("teim==>" + time);

                      List<HistogramDataPointAttribute> histogramDataPointAttributes =  histogramDataPoint.getAttributes();  

                      for ( HistogramDataPointAttribute histogramDataPointAttribute : histogramDataPointAttributes ){

                         if (keys == null) {
                          keys = new StringBuffer();
                        }
                          String keyValue = histogramDataPointAttribute.getKey();
                          String attvalue = null;

                          HistogramDataPointAttributeValue gAttValue = histogramDataPointAttribute.getValue();

                          if (gAttValue != null)
                          {

                             attvalue = gAttValue.getStringValue();

                          }else 
                          {
                            attvalue = null;
                          }
                          if ( keyValue!=null && attvalue != null ) {

                              keys.append("( ");
                              keys.append(keyValue); 
                              keys.append(":");
                              keys.append(attvalue);
                              keys.append(" )");
                            

                          }
                          
                      
                      }
                     //
                    keplerMetricDTO = new KeplerMetricDTO();

                    keplerMetricDTO.setDate(createdTime);
                    keplerMetricDTO.setPowerConsumption(usage);
                    keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
                    keplerMetricDTO.setServiceName(keys != null  ?  keys.toString() : metricName);
                    keplerMetricDTO.setType(type);
                    keplerMetricDTOLst.add(keplerMetricDTO);

                }

               
              }
              

           }

          }
        return keplerMetricDTOLst ;
      }
  private static Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
        long observedTimeMillis = Long.parseLong(startTimeUnixNano) / 1_000_000;

        Instant instant = Instant.ofEpochMilli(observedTimeMillis);

        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

        return Date.from(istDateTime.atZone(istZone).toInstant());
        
    }


      public void addDTO(){
        // KeplerMetricDTO keplerMetricDTO = new KeplerMetricDTO();

        // keplerMetricDTO.setDate(createdTime);
        // keplerMetricDTO.setPowerConsumption(usage);
        // keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
        // keplerMetricDTO.setServiceName(key == null ? metricName : key);
        // keplerMetricDTO.setType(type);
        // keplerMetricDTOLst.add(keplerMetricDTO);

       }

}
