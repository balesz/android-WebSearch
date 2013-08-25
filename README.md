WebSearch
=================

WebSearch is a very simple and useful application. If you ever used custom search engines in Firefox or in Chrome, then you'll love this app. It works like the search bar in the Firefox browser. You can add search engines, then you can use them to search any text you want through the Android share API. The app can handle the OpenSearch format.

### Admin UI
You can manage your search engines on the admin UI. Using the admin UI is very easy. You can import engines from an url, or you can add new engines by hand. If you tap the import icon on the action bar, a dialog will appear. You have to fill the url edit box, then tap the import button. If the given site has an OpenSearch format search provider, the app will import it. The search engine has four fields: name, url, image url and description. You have to fill the name and the url fields. It is a paste icon on the right side of the url field. If you tap this icon,  it will paste a "{searchTerm}" string into the url field. This is necessary because this string will be replaced by your search string in the url.

### Share UI
The app uses the Share API of Android. This means that you can search any string in any app, provided the app allows plain text sharing. For example, you are reading an article in feedly or in Pocket. You have to do the followings:
- Select any text in the article (tap and hold on the text)
- Tap the share icon
- Select the WebSearch app from the list
- Select a search engine
- The selected engine will open in your browser
That's it.

I’m planning an import function. This function will be able to import OpenSearch engines from http://mycroftproject.com and http://www.searchplugins.net.
