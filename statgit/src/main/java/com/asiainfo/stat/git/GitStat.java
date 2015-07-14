package com.asiainfo.stat.git;

/**
 * Created by Jacky on 2015/7/7.
 *
 * Git Stat Service
 */

import com.asiainfo.constant.Constants;
import com.asiainfo.service.IGitService;
import com.asiainfo.stat.vo.StatData;
import com.asiainfo.stat.vo.StatParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GitStat implements IGitService {

    private Pattern pat = Pattern.compile("(\\d+)\\s(\\d+)\\s");

    @Override
    public JSONArray stat(String method, String startDate,
                          String endDate, String author, String gitRoot) throws Exception {

        Stack<String> result = multiCmdExec(method, startDate, endDate, author, gitRoot);
        StatParam statParam = calcDays(method, startDate, endDate);

        boolean start = true;
        long add = 0, del = 0, num = 0;

        Map<String, StatData> stat = new HashMap<String, StatData>();
        StatData sd = new StatData();
        while (!result.empty()) {
            String line = result.pop();
//            System.out.println(line);

            Matcher m = pat.matcher(line);
            if (m.find()) {
                MatchResult mr = m.toMatchResult();
                add += Long.parseLong(mr.group(1));
                del += Long.parseLong(mr.group(2));
                num += 1;
            } else {
                String[] infos = line.split(";");
                String name = infos[0];
                String date = infos[1];

                sd.setName(name);
                sd.setAdd(add);
                sd.setDel(del);
                sd.setFile(num);
                sd.setFirst(date);
                sd.setLast(date);
                sd.initDetail(new Long(statParam.getDays()).intValue());
                sd.setDetail(statParam.calcDays(date), add, del);

                if (stat.containsKey(name)) {
                    StatData msd = stat.get(name);
                    stat.put(name, msd.addData(sd));
                } else {
                    stat.put(name, sd);
                }

                add = 0;
                del = 0;
                num = 0;
                sd = new StatData();
            }
        }

        List<StatData> gsd = new ArrayList<StatData>();
        for (Iterator<String> it = stat.keySet().iterator(); it.hasNext();) {
            String name = it.next();
            StatData msd = stat.get(name);
            gsd.add(msd);
        }

        Collections.sort(gsd);

        JSONArray sdArray = new JSONArray();
        JsonConfig config = new JsonConfig();
        config.setIgnoreDefaultExcludes(false);
        config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        config.setExcludes(new String[]{"days"});

        for (StatData msd : gsd) {
            JSONObject md = JSONObject.fromObject(msd/*, config*/);
            sdArray.add(md);
        }
        return sdArray;
    }

    /**
     * calculate the differece between the startDate and endDate, and init the detail array
     *
     * @param method
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    private StatParam calcDays(String method, String startDate, String endDate) throws ParseException {
        StatParam sp = new StatParam();
        long days = 0;
        Date startTime = null;
        Date lastTime = null;
        Calendar cur = Calendar.getInstance();
        switch(Integer.parseInt(method)) {
            case Constants.GitConstants.STAT_METHOD_CUSTOM:
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.StaticParam.SHORT_DATE_FORMAT);
                startTime = sdf.parse(startDate);
                lastTime =  sdf.parse(endDate);
                days = (lastTime.getTime()-startTime.getTime())/Constants.StaticParam.DAY_MILLIONSECOND;
                days += 1;
                break;
            case Constants.GitConstants.STAT_METHOD_DAY:
                lastTime = cur.getTime();
                cur.add(Calendar.DATE, -1);
                startTime = cur.getTime();
                days = 1;
                break;
            case Constants.GitConstants.STAT_METHOD_WEEK:
                lastTime = cur.getTime();
                cur.add(Calendar.DATE, - 7);
                startTime = cur.getTime();
                days = 7;
                break;
            case Constants.GitConstants.STAT_METHOD_MONTH:
                Date today = new Date();
                lastTime = cur.getTime();
                cur.add(Calendar.MONTH, -1);
                startTime = cur.getTime();
                days = (lastTime.getTime()-startTime.getTime())/Constants.StaticParam.DAY_MILLIONSECOND;
                break;
            default:
                break;
        }
        sp.setStartDate(startTime);
        sp.setEndDate(lastTime);
        sp.setDays(days);
        return sp;
    }

    /**
     * execute git log bash command
     *
     * @param method
     * @param startDate
     * @param endDate
     * @param author
     * @return
     * @throws IOException
     */
    private Stack<String> multiCmdExec(String method, String startDate,
                                      String endDate, String author, String gitRoot) throws IOException {
        Stack<String> s = new Stack<String>();
        String since = null;
        String until = null;
        String committer = null;

        switch(Integer.parseInt(method)) {
            case Constants.GitConstants.STAT_METHOD_CUSTOM:
                since = startDate;
                until = endDate;
                break;
            case Constants.GitConstants.STAT_METHOD_DAY:
                since = "1.day.ago";
                break;
            case Constants.GitConstants.STAT_METHOD_WEEK:
                since = "1.week.ago";
                break;
            case Constants.GitConstants.STAT_METHOD_MONTH:
                since = "1.month.ago";
                break;
            default:
                break;
        }

        String cmd = "git log --pretty=format:\"%cn;%ad;%d\" --numstat --date=iso"
                + " --since=" + since;
        cmd += (until != null && !until.equals("")) ? (" --until=" + until) : "";
//                + " --until=" + until;
//			+ " --committer=" + committer;

        System.out.println("cmd=>" + cmd);
        gitRoot = gitRoot.replaceAll("//", File.separator);
        gitRoot = gitRoot.replaceAll("\\\\", "\\" + File.separator);

        try {
            Process process = null;
            String os = System.getProperty("os.name");
            if(os.toLowerCase().startsWith("win")){
                process = Runtime.getRuntime().exec("cmd", null, new File(gitRoot));
            } else {
                process = Runtime.getRuntime().exec("sh", null, new File(gitRoot));
            }

            SequenceInputStream sis = new SequenceInputStream(
                    process.getInputStream(), process.getErrorStream());
            InputStreamReader isr = new InputStreamReader(sis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            // next command
            OutputStreamWriter osw = new OutputStreamWriter( process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(cmd);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            // read
            String line = null;
            String mid = null;
            boolean r = false;
            while (null != (line = br.readLine())) {

                if (!line.startsWith("-")) {
                    Matcher m = pat.matcher(line);
                    if (m.find()) {
                        if (mid != null && !r) {
                            s.add(mid.replace(" +0800", ""));
                        }
                        s.add(line);
                        r = true;
                    } else {
                        if (r) {
                            r = false;
                        }
                        mid = line;
                    }
                }
            }
            process.destroy();
            br.close();
            isr.close();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
