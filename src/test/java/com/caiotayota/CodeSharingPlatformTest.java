import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.dynamic.input.DynamicTestingMethod;
import org.hyperskill.hstest.exception.outcomes.PresentationError;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.caiotayota.platform.CodeSharingPlatform;

import java.util.HashMap;
import java.util.Map;

import static org.hyperskill.hstest.common.Utils.sleep;
import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isArray;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isInteger;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isNumber;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isObject;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.isString;

public class CodeSharingPlatformTest extends SpringTest {
    public CodeSharingPlatformTest() {
        super(CodeSharingPlatform.class, "../snippets.mv.db");
    }

    final String API_CODE = "/api/code/";
    final String WEB_CODE = "/code/";

    final String API_CODE_NEW = "/api/code/new";
    final String WEB_CODE_NEW = "/code/new";

    final String API_LATEST = "/api/code/latest";
    final String WEB_LATEST = "/code/latest";

    final String[] SNIPPETS = {
        "public static void ...",
        "class Code { ...",
        "Snippet #3",
        "Snippet #4",
        "Snippet #5",
        "Snippet #6",
        "Snippet #7",
        "Snippet #8",
        "Snippet #9",
        "Snippet #10",
        "Snippet #11",
        "Snippet #12",
        "Snippet #13",
        "Snippet #14",

        "Snippet #15",
        "Snippet #16",
        "Snippet #17",
        "Snippet #18",
        "Snippet #19",
        "Snippet #20",
        "Snippet #21",
        "Snippet #22",
    };

    final Map<Integer, String> ids = new HashMap<>();
    final Map<Integer, String> dates = new HashMap<>();
    final Map<Integer, Integer> secs = new HashMap<>();
    final Map<Integer, Integer> views = new HashMap<>();

    boolean checkSecret = false;
    long freezeTime = 0;
    long awaitTime = 0;
    long sleepDurationSec = 0;
    long sleepLowerBound = 0;
    long sleepUpperBound = 0;

    static String th(int val) {
        if (val == 1) {
            return "" + val + "st";
        } else if (val == 2) {
            return "" + val + "nd";
        } else if (val == 3) {
            return "" + val + "rd";
        } else {
            return "" + val + "th";
        }
    }

    static void checkStatusCode(HttpResponse resp, int status) {
        if (resp.getStatusCode() != status) {
            throw new WrongAnswer(
                resp.getRequest().getMethod() + " " +
                    resp.getRequest().getLocalUri() +
                    " should respond with status code " + status + ", " +
                    "responded: " + resp.getStatusCode() + "\n\n" +
                    "Response body:\n\n" + resp.getContent()
            );
        }
    }

    static void checkTitle(Document doc, String url, String title) {
        if (!doc.title().equals(title)) {
            throw new WrongAnswer("GET " + url +
                " should contain title \"" + title + "\"");
        }
    }

    static Element getSingleTag(Element doc, String url, String tag) {
        Elements elems = getElemsByTag(doc, url, tag, 1);
        return elems.get(0);
    }

    static Elements getElemsByTag(Element doc, String url, String tag, int length) {
        Elements elems = doc.getElementsByTag(tag);
        if (elems.size() != length) {
            throw new WrongAnswer("GET " + url +
                " should contain " + length + " <" + tag + "> " +
                "element"+ (length == 1 ? "" : "s") +", found: " + elems.size());
        }
        return elems;
    }

    static Element getById(Element doc, String url, String id, String tag) {
        Element elem = doc.getElementById(id);

        if (elem == null) {
            throw new WrongAnswer("GET " + url +
                " should contain an element with id \"" + id + "\", no one found");
        }

        if (!elem.tagName().equals(tag)) {
            throw new WrongAnswer("GET " + url +
                " should contain an element with id  \"" + id + "\" and tag \"" + tag + "\", " +
                "found another tag: \"" + elem.tagName() + "\"");
        }

        return elem;
    }

    static void checkMissingId(Element doc, String url, String id) {
        Element elem = doc.getElementById(id);
        if (elem != null) {
            throw new WrongAnswer("GET " + url +
                " shouldn't contain an element with id \"" + id + "\", but one was found");
        }
    }

    @DynamicTestingMethod
    public DynamicTesting[] dt = new DynamicTesting[] {
        // test 1
        this::checkWebCodeNew,

        // test 2
        () -> postSnippet(0),
        () -> checkApiCode(0),
        () -> checkWebCode(0),
        () -> checkApiLatest(0),
        () -> checkWebLatest(0),

        // test 7
        () -> postSnippet(1),
        () -> checkApiCode(0),
        () -> checkWebCode(0),
        () -> checkApiCode(1),
        () -> checkWebCode(1),
        () -> checkApiLatest(1, 0),
        () -> checkWebLatest(1, 0),

        // test 14
        () -> postSnippet(2),
        () -> postSnippet(3),
        () -> postSnippet(4),
        () -> postSnippet(5),
        () -> postSnippet(6),
        () -> postSnippet(7),
        () -> postSnippet(8),
        () -> postSnippet(9),
        () -> postSnippet(10),
        () -> postSnippet(11),
        () -> postSnippet(12),
        () -> postSnippet(13),

        // test 26
        () -> checkApiCode(0),
        () -> checkWebCode(0),
        () -> checkApiCode(1),
        () -> checkWebCode(1),
        () -> checkApiCode(2),
        () -> checkWebCode(2),
        () -> checkApiCode(3),
        () -> checkWebCode(3),
        () -> checkApiCode(4),
        () -> checkWebCode(4),
        () -> checkApiCode(5),
        () -> checkWebCode(5),
        () -> checkApiCode(6),
        () -> checkWebCode(6),
        () -> checkApiCode(7),
        () -> checkWebCode(7),
        () -> checkApiCode(8),
        () -> checkWebCode(8),
        () -> checkApiCode(9),
        () -> checkWebCode(9),
        () -> checkApiCode(10),
        () -> checkWebCode(10),
        () -> checkApiCode(11),
        () -> checkWebCode(11),
        () -> checkApiCode(12),
        () -> checkWebCode(12),
        () -> checkApiCode(13),
        () -> checkWebCode(13),

        // test 54
        () -> checkApiLatest(13, 12, 11, 10, 9, 8, 7, 6, 5, 4),
        () -> checkWebLatest(13, 12, 11, 10, 9, 8, 7, 6, 5, 4),

        // test 56
        this::reloadServer,

        // test 57
        () -> checkApiCode(0),
        () -> checkWebCode(0),
        () -> checkApiCode(1),
        () -> checkWebCode(1),
        () -> checkApiCode(2),
        () -> checkWebCode(2),
        () -> checkApiCode(3),
        () -> checkWebCode(3),
        () -> checkApiCode(4),
        () -> checkWebCode(4),
        () -> checkApiCode(5),
        () -> checkWebCode(5),
        () -> checkApiCode(6),
        () -> checkWebCode(6),
        () -> checkApiCode(7),
        () -> checkWebCode(7),
        () -> checkApiCode(8),
        () -> checkWebCode(8),
        () -> checkApiCode(9),
        () -> checkWebCode(9),
        () -> checkApiCode(10),
        () -> checkWebCode(10),
        () -> checkApiCode(11),
        () -> checkWebCode(11),
        () -> checkApiCode(12),
        () -> checkWebCode(12),
        () -> checkApiCode(13),
        () -> checkWebCode(13),

        // test 85
        () -> checkApiLatest(13, 12, 11, 10, 9, 8, 7, 6, 5, 4),
        () -> checkWebLatest(13, 12, 11, 10, 9, 8, 7, 6, 5, 4),

        // test 87
        () -> postSnippet(14),
        () -> postSnippet(15, 100, 20),
        () -> postSnippet(16),
        () -> postSnippet(17, 0, 5),
        () -> postSnippet(18),
        () -> postSnippet(19, 3, 0),
        () -> postSnippet(20),
        () -> postSnippet(21, 30, 0),

        // test 95
        () -> checkApiCode(14),
        () -> checkWebCode(14),
        () -> checkApiCode(16),
        () -> checkWebCode(16),
        () -> checkApiCode(18),
        () -> checkWebCode(18),
        () -> checkApiCode(20),
        () -> checkWebCode(20),

        // test 103
        () -> {
            freezeTime = System.currentTimeMillis();
            sleep(5000);
            return reloadServer();
        },

        // test 104
        () -> {
            awaitTime = System.currentTimeMillis();
            sleepDurationSec = (awaitTime - freezeTime) / 1000;
            sleepLowerBound = sleepDurationSec;
            sleepUpperBound = sleepLowerBound + 10;
            checkSecret = true;
            return CheckResult.correct();
        },

        // test 105
        () -> checkApiCode(15),
        () -> checkWebCode(15),
        () -> checkApiCode(17),
        () -> checkWebCode(17),
        () -> checkApiCode404(19),
        () -> checkWebCode404(19),
        () -> checkApiCode(21),
        () -> checkWebCode(21),

        // test 113
        () -> checkApiLatest(20, 18, 16, 14, 13, 12, 11, 10, 9, 8),
        () -> checkWebLatest(20, 18, 16, 14, 13, 12, 11, 10, 9, 8),

        // test 115
        () -> checkApiCode(17),
        () -> checkApiCode(17),
        () -> checkWebCode(17),
        () -> checkApiCode404(17),
        () -> checkWebCode404(17),
    };

    private CheckResult checkApiCode(int id) {
        String codeId = ids.get(id);
        String snippet = SNIPPETS[id];
        int time = secs.get(id);
        int views = this.views.get(id);

        HttpResponse resp = get(API_CODE + codeId).send();
        checkStatusCode(resp, 200);

        expect(resp.getContent()).asJson().check(
            isObject()
                .value("code", snippet)
                .value("date", isString(s -> {
                    if (dates.containsKey(id)) {
                        return s.equals(dates.get(id));
                    }
                    dates.put(id, s);
                    return true;
                }))
                .value("time", isInteger(i -> {
                    if (!checkSecret || time == 0) {
                        return i == 0;
                    }
                    int upperBound = (int) (time - sleepLowerBound);
                    int lowerBound = (int) (time - sleepUpperBound);
                    return i >= lowerBound && i <= upperBound;
                }))
                .value("views", isInteger(i -> {
                    if (!checkSecret || views == 0) {
                        return i == 0;
                    }
                    boolean result = i == views - 1;
                    this.views.put(id, views - 1);
                    return result;
                }))

        );

        return CheckResult.correct();
    }

    private CheckResult checkApiCode404(int id) {
        HttpResponse resp = get(API_CODE + ids.get(id)).send();
        checkStatusCode(resp, 404);
        return CheckResult.correct();
    }

    private CheckResult checkWebCode(int id) {
        String codeId = ids.get(id);
        String apiSnippet = SNIPPETS[id];
        String apiDate = dates.get(id);
        int time = secs.get(id);
        int views = this.views.get(id);

        String req = WEB_CODE + codeId;
        HttpResponse resp = get(req).send();
        checkStatusCode(resp, 200);

        String html = resp.getContent();
        Document doc = Jsoup.parse(html);

        checkTitle(doc, req, "Code");

        Element pre = getById(doc, req, "code_snippet", "pre");
        Element code = getSingleTag(pre, req, "code");

        String webSnippetCode = code.text();
        if (!webSnippetCode.trim().equals(apiSnippet.trim())) {
            return CheckResult.wrong("Web code snippet " +
                "and api code snippet are different");
        }

        Element date = getById(doc, req, "load_date", "span");
        String webSnippetDate = date.text();
        if (!webSnippetDate.trim().equals(apiDate.trim())) {
            return CheckResult.wrong("Web snippet date " +
                "and api snippet date are different");
        }

        if (time != 0) {
            Element timeSpan = getById(doc, req, "time_restriction", "span");
            String timeText = timeSpan.text();
            int timeOnPage;
            try {
                timeOnPage = expect(timeText).toContain(1).integers().get(0);
            } catch (PresentationError ex) {
                return CheckResult.wrong(
                    "GET " + req + " cannot find number of seconds inside \"time_restriction\" span element.\n" +
                    "Full text:\n" + timeSpan
                );
            }
            int upperBound = (int) (time - sleepLowerBound);
            int lowerBound = (int) (time - sleepUpperBound);

            if (!(timeOnPage >= lowerBound && timeOnPage <= upperBound)) {
                return CheckResult.wrong("GET " + req + " should " +
                    "contain time restriction between " + lowerBound
                    + " and " + upperBound + ", found: " + timeOnPage + "\n" +
                    "Full text:\n" + timeSpan);
            }
        } else {
            checkMissingId(doc, req, "time_restriction");
        }

        if (views != 0) {
            Element viewsSpan = getById(doc, req, "views_restriction", "span");
            String viewsText = viewsSpan.text();
            int viewsOnPage = expect(viewsText).toContain(1).integers().get(0);

            if (viewsOnPage != views - 1) {
                return CheckResult.wrong("GET " + req + " should " +
                    "contain views restriction equal to " + (views - 1)
                    + ", found: " + viewsOnPage + "\n" +
                    "Full text:\n" + viewsSpan);
            }
            this.views.put(id, views - 1);
        } else {
            checkMissingId(doc, req, "views_restriction");
        }

        return CheckResult.correct();
    }

    private CheckResult checkWebCode404(int id) {
        HttpResponse resp = get(WEB_CODE + ids.get(id)).send();
        checkStatusCode(resp, 404);
        return CheckResult.correct();
    }

    private CheckResult checkWebCodeNew() {
        HttpResponse resp = get(WEB_CODE_NEW).send();
        checkStatusCode(resp, 200);

        String html = resp.getContent();
        Document doc = Jsoup.parse(html);

        checkTitle(doc, WEB_CODE_NEW, "Create");

        getById(doc, WEB_CODE_NEW, "code_snippet", "textarea");
        getById(doc, WEB_CODE_NEW, "send_snippet", "button");

        return CheckResult.correct();
    }

    private CheckResult postSnippet(int id) {
        return postSnippet(id, 0, 0);
    }

    private CheckResult postSnippet(int id, int secs, int views) {
        String snippet = SNIPPETS[id];
        this.secs.put(id, secs);
        this.views.put(id, views);

        HttpResponse resp = post(API_CODE_NEW,
            "{\"code\":\"" + snippet + "\", " +
                "\"time\": " + secs +", " +
                "\"views\": " + views + "}").send();
        checkStatusCode(resp, 200);

        expect(resp.getContent()).asJson().check(
            isObject()
                .value("id", isString(i -> {
                    if (i.length() != 36) {
                        return false;
                    }
                    ids.put(id, "" + i);
                    return true;
                }))
        );

        return CheckResult.correct();
    }

    private CheckResult checkApiLatest(int... ids) {
        String req = API_LATEST;
        HttpResponse resp = get(req).send();
        checkStatusCode(resp, 200);

        expect(resp.getContent()).asJson().check(
            isArray(ids.length, isObject()
                .value("code", isString())
                .value("date", isString())
                .value("time", isInteger(i -> i == 0,
                    "should be equal to 0 (latest snippets should not contain time restrictions)"))
                .value("views", isInteger(i -> i == 0,
                    "should be equal to 0 (latest snippets should not contain time restrictions)"))
            )
        );

        JsonArray elem = resp.getJson().getAsJsonArray();
        for (int i = 0; i < ids.length; i++) {
            JsonObject item = elem.get(i).getAsJsonObject();

            String actualSnippet = SNIPPETS[ids[i]];
            String actualDate = dates.get(ids[i]);

            String givenSnippet = item.get("code").getAsString();
            String givenDate = item.get("date").getAsString();

            if (!actualSnippet.equals(givenSnippet)) {
                return CheckResult.wrong("GET " + req + " " + th(i + 1) +
                    " snippet doesn't match actual snippet.\n" +
                    "Expected:\n" + actualSnippet + "\n" +
                    "Found:\n" + givenSnippet);
            }

            if (!actualDate.equals(givenDate)) {
                return CheckResult.wrong("GET " + req + " " + th(i + 1) +
                    " snippet's date doesn't match actual snippet's date.\n" +
                    "Expected:\n" + actualDate + "\n" +
                    "Found:\n" + givenDate);
            }
        }

        return CheckResult.correct();
    }

    private CheckResult checkWebLatest(int... ids) {
        String req = WEB_LATEST;
        HttpResponse resp = get(req).send();
        checkStatusCode(resp, 200);

        String html = resp.getContent();
        Document doc = Jsoup.parse(html);

        checkTitle(doc, req, "Latest");

        Elements preList = getElemsByTag(doc, req, "pre", ids.length);
        Elements spanList = getElemsByTag(doc, req, "span", ids.length);

        for (int i = 0; i < ids.length; i++) {
            String apiSnippet = SNIPPETS[ids[i]];
            String apiDate = dates.get(ids[i]);
            Element pre = preList.get(i);

            String webSnippetCode = pre.text();
            if (!webSnippetCode.trim().equals(apiSnippet.trim())) {
                return CheckResult.wrong("GET " + req + " " + th(i + 1)
                    + " snippet doesn't match " + th(i + 1) + " snippet via api.\n" +
                    "Expected:\n" + apiSnippet + "\n" +
                    "Found:\n" + webSnippetCode);
            }

            Element date = spanList.get(i);
            String webSnippetDate = date.text();
            if (!webSnippetDate.trim().equals(apiDate.trim())) {
                return CheckResult.wrong("GET " + req + " " + th(i + 1)
                    + " snippet's date doesn't match " + th(i + 1)
                    + " snippet's date via api.\n" +
                    "Expected:\n" + apiDate + "\n" +
                    "Found:\n" + webSnippetDate);
            }
        }

        return CheckResult.correct();
    }

    private CheckResult reloadServer() {
        try {
            reloadSpring();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return CheckResult.correct();
    }
}
