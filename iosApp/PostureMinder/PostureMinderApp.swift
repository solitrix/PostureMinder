//
//  PostureMinderApp.swift
//  PostureMinder
//
//  Created by Bren on 25/05/2026.
//

import SwiftUI
import sharedKit

@main
struct PostureMinderApp: App {
    private let viewModel = PostureViewModelFactory().create()

    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: viewModel)
        }
    }
}
