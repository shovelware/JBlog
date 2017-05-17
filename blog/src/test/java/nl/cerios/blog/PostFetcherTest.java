package nl.cerios.blog;

import junit.framework.TestCase;
import nl.cerios.blog.database.PostFetcher;

public class PostFetcherTest extends TestCase {

	PostFetcher fetcher = new PostFetcher();
	
	public void testFetchRecentPosts() {
		fetcher.fetchRecentPosts(5);
	}

}
