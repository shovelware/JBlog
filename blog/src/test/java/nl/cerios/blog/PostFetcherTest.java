package nl.cerios.blog;

import junit.framework.TestCase;

public class PostFetcherTest extends TestCase {

	PostFetcher fetcher = new PostFetcher();
	
	public void testFetchRecentPosts() {
		fetcher.fetchRecentPosts(5);
	}

}
