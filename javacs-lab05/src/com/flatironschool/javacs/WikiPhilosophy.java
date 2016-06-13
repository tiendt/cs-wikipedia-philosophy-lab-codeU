package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;
import java.util.Stack;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();
	final static List<String> allLinks = new ArrayList<String>();
	final static Stack<Character> stack = new Stack<Character>();

	/**
	* Tests a conjecture about Wikipedia and Philosophy.
	*
	* https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	*
	* 1. Clicking on the first non-parenthesized, non-italicized link
	* 2. Ignoring external links, links to the current page, or red links
	* 3. Stopping when reaching "Philosophy", a page with no links or a page
	*    that does not exist, or when a loop occurs
	*
	* @param args
	* @throws IOException
	*/
	public static void main(String[] args) throws IOException {
		// some example code to get you started

		String startUrl = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String endUrl = "https://en.wikipedia.org/wiki/Philosophy";
		getEndUrl (startUrl, endUrl);
	}

	public static void getEndUrl (String start, String end) throws IOException {
		String link = start;

		for (int i = 0; i < 10; i++) {
			if (allLinks.contains(link)) {
				System.err.println ("Link visited");
			}
			else {
				allLinks.add(link);
			}
			Element element = getFirst(link);
			if (element == null) {
				System.out.println ("null link");
			}
			else {
				System.out.println ("first: " + element.text());
				link = "https://en.wikipedia.org" + element.attr("href");
				if (link.equals(end)) {
					System.out.println ("Found Philosophy");
					break;
				}
			}
		}
	}

	public static Element getFirst (String url) throws IOException {
		Elements paragraphs = wf.fetchWikipedia(url);
		return findFirstLink (paragraphs);
	}

	public static Element findFirstLink (Elements paragraphs) {
		for (Element paragraph: paragraphs) {
			Iterable<Node> iter = new WikiNodeIterable(paragraph);
			for (Node node: iter) {
				if (node instanceof TextNode) {
					System.out.println (node);
					checkParenthesis (((TextNode) node).text());
				}
				if (node instanceof Element) {
					Element newNode = (Element) node;
					if (newNode.tagName().equals("a") && stack.isEmpty()) {
						Elements parents = newNode.parents();
						if (!(parents.hasAttr("i") && parents.hasAttr("em"))) {
							return newNode;
						}
					}
				}
			}
		}
		return null;
	}

	public static void checkParenthesis(String str)
	{
		if (str.isEmpty()) {
			System.out.println ("no string inserted");
		}
		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			if (current == '(') {
				stack.push(current);
			}
			if (current == ')') {
				char last = stack.peek();
				if (current == ')' && last == '(') {
					stack.pop();
				}
				else {
					System.out.println ("unbalanced");
				}
			}
		}
	}
}
