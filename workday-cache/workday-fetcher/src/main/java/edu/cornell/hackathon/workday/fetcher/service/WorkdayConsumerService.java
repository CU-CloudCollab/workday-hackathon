package edu.cornell.hackathon.workday.fetcher.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import edu.cornell.hackathon.workday.distcache.DistCache;
import edu.cornell.hackathon.workday.fetcher.config.Config;
import edu.cornell.hackathon.workday.jobandperson.model.ReportDataType;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorkdayConsumerService {

    private final Config config;
    private final DistCache distCache;

    public WorkdayConsumerService(final Config config, final DistCache distCache) {
        this.config = config;
        this.distCache = distCache;
    }

    public boolean fetchWorkdayData() {
        boolean success = true;
        OkHttpClient client = new OkHttpClient();
        //TODO decrypt password
        String creds = Credentials.basic(config.getUsername(), config.getPassword());

        Unmarshaller unmarshaller;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(
                    "edu.cornell.hackathon.workday.hr.model:edu.cornell.hackathon.workday.jobandperson.model");
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Map<String, ReportDataType> reportsByService = new HashMap<>();
        for (Entry<String, String> service : config.getServiceMap().entrySet()) {
            Request request = new Request.Builder().url(service.getValue()).header("Authorization", creds).build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }

            if (response != null && response.isSuccessful()) {
                
                
                ReportDataType reportData = null;
                try {
                    XMLInputFactory xif = XMLInputFactory.newFactory();
                    XMLStreamReader xsr = xif.createXMLStreamReader(response.body().byteStream());
                    xsr.nextTag(); // Advance to Envelope tag
                    xsr.nextTag(); // Advance to Body tag
                    xsr.nextTag(); // Advance to getNumberResponse tag

                    JAXBElement<ReportDataType> reportDataJaxb = unmarshaller
                            .unmarshal(xsr, ReportDataType.class);
                    
                    reportData = reportDataJaxb.getValue();
                    reportsByService.put(service.getKey(), reportData);
                    
                    System.out.println(reportData);
                } catch (JAXBException | XMLStreamException e) {
                    success = false;
                }
            }
        }
        
        if (!reportsByService.isEmpty()) {
            for (Entry<String, ReportDataType> reports : reportsByService.entrySet()) {
                distCache.clearAll(reports.getKey());
                try {
                    distCache.store(reports.getKey(), reports.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }
        }

        return success;
    }
}
