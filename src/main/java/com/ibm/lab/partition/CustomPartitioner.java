package com.ibm.lab.partition;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomPartitioner extends DefaultPartitioner  {

	Logger logger = LoggerFactory.getLogger(CustomPartitioner.class);

	private String companyName;
	

	public void configure(Map<String, ?> configs) {
		companyName = configs.get("lab.company").toString();
	}


	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int sp = (int) Math.abs(numPartitions * 0.3);
        int p = 0;

        if ((keyBytes != null) && (key instanceof String)) {
            if (((String) key).equals(companyName)) {
              p = 0;
          	
              logger.info("Key = {}, Partition = {}",(String) key , p); 
              
              return p;
          } 
        } 
        
        return super.partition(topic, key, keyBytes, value, valueBytes, cluster);
	}
}
