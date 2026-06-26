//
//  ContentView.swift
//  PostureMinder
//
//  Created by Bren on 25/05/2026.
//

import SwiftUI
import sharedKit

struct ContentView: View {
    let viewModel: PostureViewModel

    var body: some View {
        PostureScreenView(viewModel: viewModel)
            .ignoresSafeArea()
    }
}

#Preview {
    ContentView(viewModel: PostureViewModelFactory().create())
}
