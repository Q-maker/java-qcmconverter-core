package com.qmaker.converter.core;

import com.qmaker.core.entities.Qcm;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import istat.android.base.tools.ToolKits;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void decodeTopGradeJsonString() throws FileNotFoundException {
        String source = ToolKits.Stream.streamToString(new FileInputStream("/home/istat/Temp/TopGradePseudoJson.json"));
        String htmlStringUtilEscape = StringEscapeUtils.unescapeHtml4(source);
        String htmlStringUtilDecode = StringEscapeUtils.escapeHtml4(htmlStringUtilEscape);
        assertEquals(source, htmlStringUtilDecode);
    }

    @Test
    public void parseTopGrade() throws Exception {
        String urlString = "file:///media/ext2data/Istat/Devs/AndroidStudio/Devup/Q-maker/qcmconvertercore/TopGradePagePlayQuizAnimals9.html";
//        String uriString = "https://TopgradeApp.com/playQuiz/animals-9";
        URL url = new URL(urlString);
        String source = ToolKits.Stream.streamToString(url.openStream());
        Document doc = Jsoup.parse(source);
        String nextQuestionId;
        int index = 0;
        Element elementQuestionId;
        Element elementQuestionNumber;
        List<Qcm> qcmList = new ArrayList<>();
        Qcm qcm;
        while ((elementQuestionId = doc.getElementById("questionId" + index)) != null) {
            elementQuestionNumber = doc.getElementById("questionNumber" + index);
            qcm = new Qcm();
            nextQuestionId = elementQuestionId.text();
            fillQcmMataData(qcm, doc, nextQuestionId);
            qcm.setId(com.qmaker.core.utils.ToolKits.generateID(null, nextQuestionId));
            qcm.setQuestion(createQuestion(elementQuestionNumber, nextQuestionId));
            qcm.setPropositions(createPropositionList(elementQuestionNumber, nextQuestionId));
            qcmList.add(qcm);
            System.out.println(qcm.toString());
            index++;
        }
    }

    private List<Qcm.Proposition> createPropositionList(Element doc, String nextQuestionId) {
        Elements elements = doc.getElementsByTag("button");
        List<Qcm.Proposition> propositions = new ArrayList<>();
        Qcm.Proposition proposition;
        for (Element element : elements) {
            proposition = createPropositionFromButtonElement(element);
            if (proposition != null) {
                propositions.add(proposition);
            }
        }


//        int index = 0;
//        Element elementQuestionId;
//        Qcm.Proposition proposition;
//        while ((elementQuestionId = doc.getElementById("questionId" + index)) != null) {
//            proposition = new Qcm.Proposition();
//            nextQuestionId = elementQuestionId.text();
//            //TODO fill proposition metadata
//            index++;
//        }
        return propositions;
    }

    private Qcm.Proposition createPropositionFromButtonElement(Element element) {
        Elements pElements = element.getElementsByTag("p");
        if (pElements != null && !pElements.isEmpty()) {
            Qcm.Proposition proposition = new Qcm.Proposition();
            proposition.setLabel(pElements.get(0).text());
            proposition.setTruth(element.id() != null && element.id().endsWith("1"));
            return proposition;
        }
        return null;
    }

    private List<Qcm.Proposition> createPropositionList(Document doc, String nextQuestionId) {
        List<Qcm.Proposition> propositions = new ArrayList<>();
        int index = 0;
        Element elementQuestionId;
        Qcm.Proposition proposition;
        while ((elementQuestionId = doc.getElementById("questionId" + index)) != null) {
            proposition = new Qcm.Proposition();
            nextQuestionId = elementQuestionId.text();
            //TODO fill proposition metadata
            index++;
        }
        return propositions;
    }

    private void fillQcmMataData(Qcm qcm, Document doc, String nextQuestionId) {

    }

    private void fillpropositionMataData(Qcm qcm, Document doc, String nextQuestionId) {

    }

    private Qcm.Question createQuestion(Document doc, String nextQuestionId) {
        Qcm.Question question = new Qcm.Question();
        question.setLabel(doc.getElementById("question" + nextQuestionId).text());
        return question;
    }

    private Qcm.Question createQuestion(Element doc, String nextQuestionId) {
        Qcm.Question question = new Qcm.Question();
        question.setLabel(doc.getElementById("question" + nextQuestionId).text());
        return question;
    }
}