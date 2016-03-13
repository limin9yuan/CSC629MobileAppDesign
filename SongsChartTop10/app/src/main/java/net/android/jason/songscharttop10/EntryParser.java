//------------------------------------------------------------------------------
//  File       : EntryParser.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/12/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.songscharttop10;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class EntryParser {

    public EntryParser(String xmlData) {
        this.xmlData = xmlData;
        entries = new ArrayList<>();
    }

    /**
     * Parse the RSS XML data and translate the RSS entries to the Entry
     * collection.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws IllegalStateException
     * @see #getEntries()
     */
    public void parse() throws ParserConfigurationException, SAXException,
            XPathExpressionException, IOException, IllegalStateException {

        sanityOrCrazy();

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(
                new InputSource(new StringReader(xmlData)));

        Node feed = doc.getFirstChild();
        if (false == feed.hasChildNodes())
            throw new IllegalStateException("RSS feed has no content.");

        NodeList children = feed.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node entry = children.item(i);
            Log.d(clazz, "Curr node: " + entry.getNodeName());


            if (entry.getNodeType() == Node.ELEMENT_NODE &&
                    entry.getLocalName().equals("entry")) {

                Log.d(clazz, "Found an entry at " + i);

                if (false == entry.hasChildNodes()) {
                    Log.d(clazz, "Empty entry at " + i);
                    continue;
                }

                Entry item = new Entry();

                NodeList subEntries = entry.getChildNodes();
                for (int j = 0; j < subEntries.getLength(); j++) {
                    Node sub = subEntries.item(j);
                    if (sub.getNodeType() == Node.ELEMENT_NODE) {
                        switch (sub.getLocalName().toLowerCase()) {
                            case "name":
                                item.setName(sub.getTextContent());
                                Log.d(clazz, "Entry name: " + sub.getTextContent());
                                break;
                            case "artist":
                                item.setArtist(sub.getTextContent());
                                Log.d(clazz, "Entry Artist: " + sub.getTextContent());
                                break;
                            case "releasedate":
                                item.setReleaseDate(sub.getTextContent());
                                Log.d(clazz,
                                        "Entry release date: " + sub.getTextContent());
                                break;
                            case "collection":
                                NodeList attrs = sub.getChildNodes();
                                for (int k = 0; k < attrs.getLength(); k++) {
                                    Node att = attrs.item(k);
                                    if (att.getNodeType() == Node.ELEMENT_NODE &&
                                            att.getLocalName()
                                                    .equalsIgnoreCase("name")) {
                                        item.setAlbum(att.getTextContent());
                                        Log.d(clazz, "Entry album: " +
                                                sub.getTextContent());
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }

                entries.add(item);
            }
        }

    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public String getXmlData() {
        return xmlData;
    }

    class Entry {
        private String name;
        private String artist;
        private String releaseDate;
        private String album;
        private String top10Songs;

        @Override
        public String toString() {
            return String.format("Name: %1$s%nArtist: %2$s%nRelease " +
                    "Date: %3$s%nInclude Top10 Songs: %4$s", name, artist,
                    releaseDate, top10Songs);
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlbum() {
            if (album == null)
                return "";
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getTop10Songs() {
            if (top10Songs == null)
                return "";
            return top10Songs;
        }

        public void addTop10Song(String songName) {
            if (songName == null)
                songName = "";
            top10Songs = (top10Songs == null || top10Songs.trim().equals("")) ?
                    songName.trim() : top10Songs + ";" + songName;
        }
    }


    private void sanityOrCrazy() throws IllegalStateException {
        Log.d(clazz, "Do sanity checking...");
        if (xmlData == null || xmlData.trim().equals(""))
            throw new IllegalStateException("No XML data");
        Log.d(clazz, "XML data received: " + xmlData);
    }

    private ArrayList<Entry> entries;
    private String xmlData;
    private final String clazz = getClass().getSimpleName();

}
