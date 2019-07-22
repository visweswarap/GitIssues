package com.alienvault;

import com.alienvault.resp.GitIssuesResp;
import com.alienvault.resp.Issue;
import com.alienvault.resp.TopPerDay;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GitHub Issues -------------
 *
 * Create a program that generates a report about the the Issues belonging to a
 * list of github repositories ordered by creation time, and information about
 * the day when most Issues were created.
 *
 * Input: ----- List of 1 to n Strings with Github repositories references with
 * the format "owner/repository"
 *
 *
 * Output: ------ String representation of a Json dictionary with the following
 * content:
 *
 * - "issues": List containing all the Issues related to all the repositories
 * provided. The list should be ordered by the Issue "created_at" field (From
 * oldest to newest) Each entry of the list will be a dictionary with basic
 * Issue information: "id", "state", "title", "repository" and "created_at"
 * fields. Issue entry example: { "id": 1, "state": "open", "title": "Found a
 * bug", "repository": "owner1/repository1", "created_at":
 * "2011-04-22T13:33:48Z" }
 *
 * - "top_day": Dictionary with the information of the day when most Issues were
 * created. It will contain the day and the number of Issues that were created
 * on each repository this day If there are more than one "top_day", the latest
 * one should be used. example: { "day": "2011-04-22", "occurrences": {
 * "owner1/repository1": 8, "owner2/repository2": 0, "owner3/repository3": 2 } }
 *
 *
 * Output example: --------------
 *
 * {
 * "issues": [ { "id": 38, "state": "open", "title": "Found a bug",
 * "repository": "owner1/repository1", "created_at": "2011-04-22T13:33:48Z" }, {
 * "id": 23, "state": "open", "title": "Found a bug 2", "repository":
 * "owner1/repository1", "created_at": "2011-04-22T18:24:32Z" }, { "id": 24,
 * "state": "closed", "title": "Feature request", "repository":
 * "owner2/repository2", "created_at": "2011-05-08T09:15:20Z" } ], "top_day": {
 * "day": "2011-04-22", "occurrences": { "owner1/repository1": 2,
 * "owner2/repository2": 0 } } }
 *
 * --------------------------------------------------------
 *
 * You can create the classes and methods you consider. You can use any library
 * you need. Good modularization, error control and code style will be taken
 * into account. Memory usage and execution time will be taken into account.
 *
 * Good Luck!
 */
public class Main {

    private static String GIT_API_URL = "https://api.github.com/repos/";
    private static String ISSUES = "/issues";

    /**
     * @param args String array with Github repositories with the format
     * "owner/repository"
     *
     */
    public static void main(String[] args) {
        System.out.println("Let's code!");

        try {
            GitIssuesResp resp = new GitIssuesResp();
            List<Issue> issuesByRepo = new ArrayList<Issue>();
            for (String repo : args) {
                issuesByRepo.addAll(getIssuesByRepo(repo));
            }
            resp.setIssues(issuesByRepo);

            List<String> dates = issuesByRepo.stream().map(a -> a.getCreated_at().substring(0, 10)).collect(Collectors.toList());

            Set<String> uniqDateStrings = new HashSet<>();
            Map<String, Integer> issuesCountByDateMap = new HashMap<>();

            for(String date : dates) {
                if(uniqDateStrings.add(date)){
                    issuesCountByDateMap.put(date, 1);
                } else {
                    Integer count = issuesCountByDateMap.get(date);
                    issuesCountByDateMap.put(date, count+1);
                }
            }
            LinkedHashMap<String, Integer> sortedMap = issuesCountByDateMap
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));

            Map.Entry<String, Integer> first = sortedMap.entrySet().iterator().next();
            String topDate = first.getKey();
            Integer topCount = first.getValue();

            TopPerDay top_day = new TopPerDay();
            top_day.setDay(topDate);
            //top_day.setOccurrences();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<Issue> getIssuesByRepo(String repo) throws IOException {
        URL url = new URL(GIT_API_URL + repo + ISSUES);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        output = br.readLine();
        System.out.println(output);

        Gson gson = new Gson();
        GitIssue[] gitIssuesRawResponse = gson.fromJson(output, GitIssue[].class);
        List<Issue> issues = new ArrayList<Issue>();
        for (GitIssue gitIssue : gitIssuesRawResponse){
            Issue issue = new Issue();
            issue.setId(gitIssue.getId());
            issue.setCreated_at(gitIssue.getCreated_at());
            issue.setRepository(repo);
            issue.setState(gitIssue.getState());
            issue.setTitle(gitIssue.getTitle());
            issues.add(issue);
        }

        //Arrays.asList(gitIssuesRawResponse).stream().map()
        conn.disconnect();
        return issues;
    }

}
