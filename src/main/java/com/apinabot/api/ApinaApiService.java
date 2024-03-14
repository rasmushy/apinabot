package com.apinabot.api;

import com.apinabot.api.dto.GymInfo;
import com.apinabot.api.exceptions.ServiceResult;
import com.apinabot.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.apinabot.utils.ParseUtil.parseGymInfo;
import static com.apinabot.utils.ParseUtil.parseGymsInfo;

/***
 * This class is responsible for fetching data from the API.
 *
 * @author rasmushy
 */
public class ApinaApiService {

    private final HttpClient client = HttpClient.newHttpClient();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApinaApiService.class);
    private static final String API_URL = ConfigLoader.getApiUrl();

    /**
     * This method is responsible for fetching all gyms from the API.
     *
     * @return a list of GymInfo objects
     */
    public ServiceResult<List<GymInfo>> getAllGyms() {
        try {
            URI uri = URI.create(API_URL + "gyms");
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Response getAllGyms(): {}", response);
            return new ServiceResult<>(parseGymsInfo(response.body()));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch all gyms", e);
            return new ServiceResult<>(e);
        }
    }

    /**
     * This method is responsible for fetching gyms by company name.
     * Example params: "kiipeilyareena"
     *
     * @param companyName the company name to search for
     * @return a list of GymInfo objects
     */
    public ServiceResult<List<GymInfo>> getGymsByCompany(String companyName) {
        try {
            if (companyName.contains(" ")) {
                LOGGER.error("Search cannot contain spaces");
                return new ServiceResult<>(new Exception("Search cannot contain spaces"));
            }
            URI uri = URI.create(API_URL + "gyms/company/" + companyName);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Response getGymsByCompany(): {}", response);
            return new ServiceResult<>(parseGymsInfo(response.body()));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch gyms by company", e);
            return new ServiceResult<>(e);
        }
    }

    /**
     * This method is responsible for fetching a gym by address.
     * Example params: "ruosilantie"
     *
     * @param streetName the street name to search for (not number)
     * @return a GymInfo object
     */
    public ServiceResult<GymInfo> getGymByAddress(String streetName) {
        try {
            String streetNameEncoded = streetName.replace(" ", "%20");
            URI uri = URI.create(API_URL + "gym/address/" + streetNameEncoded);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Response getGymByAddress(): {}", response);
            if (response.statusCode() == 500) {
                return new ServiceResult<>(new Exception("Gym not found"));
            }
            return new ServiceResult<>(parseGymInfo(response.body()));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch gym by address", e);
            return new ServiceResult<>(e);
        }
    }

    /**
     * This method is responsible for fetching gyms by city.
     * Example params: "helsinki"
     *
     * @param cityName the city name to search for
     * @return a list of GymInfo objects
     */
    public ServiceResult<List<GymInfo>> getGymsByCity(String cityName) {
        try {
            String cityNameEncoded = cityName.replace(" ", "%20");
            LOGGER.debug("Encoded city name: {}", cityNameEncoded);
            URI uri = URI.create(API_URL + "gyms/city/" + cityNameEncoded);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Response getGymsByCity(): {}", response);
            return new ServiceResult<>(parseGymsInfo(response.body()));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch gyms by city", e);
            return new ServiceResult<>(e);
        }
    }

    /**
     * This method is responsible for fetching gyms by additional info.
     * Example params: "ristikko konala"
     *
     * @param additionalInfos the additional info to search for, can be space between words
     * @return a list of GymInfo objects
     */
    public ServiceResult<List<GymInfo>> getGymsByInfo(String additionalInfos) {
        try {
            String additionalInfosEncoded = additionalInfos.replace(" ", "%20");
            URI uri = URI.create(API_URL + "gyms/" + additionalInfosEncoded);
            HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("Response getGymsByInfo(): {}", response);
            return new ServiceResult<>(parseGymsInfo(response.body()));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch gyms by info", e);
            return new ServiceResult<>(e);
        }
    }
}
