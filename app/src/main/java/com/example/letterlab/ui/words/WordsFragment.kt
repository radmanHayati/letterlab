package com.example.letterlab.ui.words

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letterlab.R
import com.example.letterlab.data.SortOrder
import com.example.letterlab.data.Word
import com.example.letterlab.databinding.FragmentWordsBinding
import com.example.letterlab.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordsFragment : Fragment(R.layout.fragment_words),WordAdapter.OnItemClickListener {
    private val viewModel: WordsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWordsBinding.bind(view)
        val wordAdapter = WordAdapter(this)
        binding.apply {
            recyclerViewWords.apply {
                adapter = wordAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.words.observe(viewLifecycleOwner) {
            wordAdapter.submitList(it)
        }
        setHasOptionsMenu(true)
    }
    override fun onItemClick(word: Word) {
       viewModel.onWordSelected(word)
    }

    override fun onCheckBoxClick(word: Word, isChecked: Boolean) {
       viewModel.onWordCheckedChanged(word,isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_words, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_words).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
              viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_words -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_words -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}