//
//  PostureScreenView.swift
//  PostureMinder
//

import SwiftUI
import sharedKit

struct PostureScreenView: UIViewControllerRepresentable {
    let viewModel: PostureViewModel

    func makeUIViewController(context: Context) -> UIViewController {
        PostureViewControllerKt.PostureViewController(viewModel: viewModel)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
