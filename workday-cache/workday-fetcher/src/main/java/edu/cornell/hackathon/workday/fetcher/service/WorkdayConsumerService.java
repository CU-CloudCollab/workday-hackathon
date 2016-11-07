package edu.cornell.hackathon.workday.fetcher.service;

import java.io.IOException;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.cornell.hackathon.workday.fetcher.config.Config;
import edu.cornell.hackathon.workday.jobandperson.model.ReportDataType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WorkdayConsumerService {

    private final Config config;

    public WorkdayConsumerService(final Config config) {
        this.config = config;
    }

    public void fetchWorkdayData() {
        OkHttpClient client = new OkHttpClient();

        Unmarshaller unmarshaller;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(
                    "edu.cornell.hackathon.workday.hr.model:edu.cornell.hackathon.workday.jobandperson.model");
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        for (Entry<String, String> service : config.getServiceMap().entrySet()) {
            Request request = new Request.Builder().url(service.getValue()).build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            if (response != null && response.isSuccessful()) {
                ReportDataType reportData = null;
                try {
                    reportData = (ReportDataType) unmarshaller.unmarshal(response.body().byteStream());
                } catch (JAXBException e) {
                    e.printStackTrace();
                }

                if (reportData != null) {
                    //write to hazelcast
                }
            }
        }
    }
}
